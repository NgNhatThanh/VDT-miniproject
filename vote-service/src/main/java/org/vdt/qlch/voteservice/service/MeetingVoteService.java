package org.vdt.qlch.voteservice.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vdt.commonlib.dto.MeetingHistoryMessage;
import org.vdt.commonlib.dto.UserDTO;
import org.vdt.commonlib.exception.BadRequestException;
import org.vdt.commonlib.model.MeetingHistoryType;
import org.vdt.commonlib.utils.AuthenticationUtil;
import org.vdt.commonlib.utils.Constants;
import org.vdt.commonlib.utils.JwtUtil;
import org.vdt.commonlib.utils.ScheduleUtil;
import org.vdt.qlch.voteservice.dto.request.CreateVoteDTO;
import org.vdt.qlch.voteservice.dto.request.QuestionSelectDTO;
import org.vdt.qlch.voteservice.dto.request.VoteDTO;
import org.vdt.qlch.voteservice.dto.response.*;
import org.vdt.qlch.voteservice.model.*;
import org.vdt.qlch.voteservice.producer.MeetingHistoryProducer;
import org.vdt.qlch.voteservice.repository.*;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MeetingVoteService {

    private final MeetingVoteRepository meetingVoteRepository;

    private final VoteQuestionRepository voteQuestionRepository;

    private final MeetingHistoryProducer meetingHistoryProducer;

    private final DocumentService documentService;

    private final VoteDocumentRepository voteDocumentRepository;

    private final VoteOptionRepository voteOptionRepository;

    private final VoteRepository voteRepository;

    private final ScheduleUtil scheduleUtil;

    private final VoteSelectRepository voteSelectRepository;

    private final JwtUtil jwtUtil;

    private final MeetingVoteCacheService meetingVoteCacheService;

    @Transactional
    @CacheEvict(value = "meeting-vote-list", key = "#dto.meetingId()")
    public void createVote(@Valid CreateVoteDTO dto) {
        if(dto.startTime().isAfter(dto.endTime())){
            throw new BadRequestException(Constants.ErrorCode.STARTTIME_AFTER_ENDTIME_ERROR);
        }
        String userId = AuthenticationUtil.extractUserId();
        MeetingVote meetingVote = MeetingVote.builder()
                .meetingId(dto.meetingId())
                .title(dto.title())
                .description(dto.description())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .type(MeetingVoteType.fromString(dto.type()))
                .createdBy(userId)
                .build();
        meetingVoteRepository.save(meetingVote);
        List<VoteOption> options = new ArrayList<>();
        List<VoteQuestion> questions = dto.questions().stream()
                .map(q -> {
                    VoteQuestion question = new VoteQuestion();
                    question.setVote(meetingVote);
                    question.setTitle(q.title());
                    options.addAll(q.options().stream()
                            .map(o -> {
                                VoteOption option = new VoteOption();
                                option.setQuestion(question);
                                option.setContent(o.content());
                                return option;
                            }).toList());
                    return question;
                }).toList();
        voteQuestionRepository.saveAll(questions);
        voteOptionRepository.saveAll(options);
        List<Integer> documentIds = new ArrayList<>(new HashSet<>(dto.documentIds()));
        boolean documentsExists = documentService.checkExistById(documentIds);
        if (!documentsExists) {
            throw new BadRequestException(Constants.ErrorCode.DOCUMENT_NOT_FOUND);
        }
        List<VoteDocument> documents = documentIds.stream()
                .map(id -> VoteDocument.builder()
                        .documentId(id)
                        .build())
                .toList();
        voteDocumentRepository.saveAll(documents);
        meetingHistoryProducer.send(MeetingHistoryMessage.builder()
                        .type(MeetingHistoryType.VOTE_CREATED)
                        .meetingId(dto.meetingId())
                        .content(String.format("Biểu quyết \"%s\" đã được tạo", truncate(dto.title())))
                .build());
        if(dto.startTime().isAfter(LocalDateTime.now())){
            scheduleUtil.scheduleTask(dto.startTime(), () ->
                    meetingHistoryProducer.send(MeetingHistoryMessage.builder()
                            .type(MeetingHistoryType.VOTE_STARTED)
                            .meetingId(dto.meetingId())
                            .content(String.format("Biểu quyết \"%s\" bắt đầu", truncate(dto.title())))
                    .build()));
        }
        scheduleUtil.scheduleTask(dto.endTime(), () ->
                meetingHistoryProducer.send(MeetingHistoryMessage.builder()
                        .type(MeetingHistoryType.VOTE_ENDED)
                        .meetingId(dto.meetingId())
                        .content(String.format("Biểu quyết \"%s\" đã kết thúc", truncate(dto.title())))
                        .build()));
    }

    public MeetingVoteDetailDTO getForSelection(int meetingVoteId){
        return meetingVoteCacheService.getForSelection(meetingVoteId);
    }

    @CachePut(value = "meeting-vote-list-user", key = "#userId+'-'+#meetingId")
    public List<MeetingVoteDTO> getMeetingVoteListForUser(String userId, int meetingId){
        List<MeetingVoteDTO> meetingVotes = meetingVoteCacheService.getList(meetingId);
        return meetingVotes.stream()
                .map(mv -> {
                    boolean voted = voteRepository.existsByCreatedByAndMeetingVote_Id(userId, mv.id());
                    return new MeetingVoteDTO(
                            mv.id(),
                            mv.title(),
                            mv.description(),
                            mv.startTime(),
                            mv.endTime(),
                            mv.type(),
                            voted
                    );
                })
                .toList();
    }

    @Transactional
    @CachePut(value = "meeting-vote-voted", key = "#voterId+'-'+#dto.meetingVoteId()")
    @CacheEvict(value = "meeting-vote-list-user", key = "#voterId+'-'+#dto.meetingId()")
    public boolean vote(String voterId, @Valid VoteDTO dto) {
        MeetingVote meetingVote = meetingVoteCacheService.getById(dto.meetingVoteId());
        LocalDateTime now = LocalDateTime.now();
        if(now.isBefore(meetingVote.getStartTime()) || now.isAfter(meetingVote.getEndTime())){
            throw new BadRequestException(org.vdt.qlch.voteservice.utils.Constants.ErrorCode.VOTE_OUT_OF_TIME_ERROR);
        }
        boolean voted = voteRepository.existsByCreatedByAndMeetingVote_Id(voterId, dto.meetingVoteId());
        if (voted) {
            throw new BadRequestException(org.vdt.qlch.voteservice.utils.Constants.ErrorCode.ALREADY_VOTED_ERROR);
        }
        List<VoteOption> options = new ArrayList<>();
        if(meetingVote.getType() == MeetingVoteType.PUBLIC){
            dto.questionSelections().forEach(selection -> {
                VoteQuestion question = meetingVote.getQuestions().stream()
                        .filter(q -> q.getId() == selection.questionId())
                        .toList().getFirst();
                if(question == null){
                    throw new BadRequestException(org.vdt.qlch.voteservice.utils.Constants.ErrorCode.MEETING_VOTE_NOT_FOUND);
                }
                VoteOption option = question.getOptions().stream().filter(o -> o.getId() == selection.optionId())
                        .toList().getFirst();
                if(option == null){
                    throw new BadRequestException(org.vdt.qlch.voteservice.utils.Constants.ErrorCode.MEETING_VOTE_NOT_FOUND);
                }
                options.add(option);
            });
        }
        else{
            List<VoteSelect> selects = dto.questionSelections().stream()
                    .map(sl -> new VoteSelect(null, sl.optionId())).toList();
            voteSelectRepository.saveAll(selects);
        }
        Vote vote = Vote.builder()
                .createdAt(LocalDateTime.now())
                .createdBy(voterId)
                .meetingVote(meetingVote)
                .selectedOptions(options)
                .build();
        if(meetingVote.getType() == MeetingVoteType.PUBLIC) meetingVoteCacheService.setUserSelections(voterId, dto);
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();
        UserDTO user = jwtUtil.extractUser(jwt);

        VoteStatusDTO voteStatusDTO = meetingVoteCacheService.getVoteStatusInCache(meetingVote.getId());
        if(voteStatusDTO == null){
            voteStatusDTO = meetingVoteCacheService.getVoteStatusInDB(meetingVote.getId());
        }
        meetingVoteCacheService.updateVoteStatus(meetingVote.getId(),
                dto.questionSelections(),
                meetingVote.getType() == MeetingVoteType.PUBLIC ? user : null);

        voteRepository.save(vote);
        meetingHistoryProducer.send(MeetingHistoryMessage.builder()
                        .meetingId(meetingVote.getMeetingId())
                        .type(MeetingHistoryType.VOTE_VOTED)
                        .content(meetingVote.getType() == MeetingVoteType.PUBLIC ?
                                String.format("%s đã bỏ phiếu cho biểu quyết \"%s\"",
                                        user.fullName(),
                                        truncate(meetingVote.getTitle())) :
                                String.format("Biểu quyết \"%s\" có phiếu bầu mới", meetingVote.getTitle()))
                .build());
        return true;
    }

    public MeetingVoteStatusDTO getStatus(int meetingVoteId){
        MeetingVoteDetailDTO detailDTO = meetingVoteCacheService.getForSelection(meetingVoteId);
        UserSelectionsDTO userSelectionsDTO = null;
        String userId = AuthenticationUtil.extractUserId();
        if(detailDTO.type() == MeetingVoteType.PUBLIC){
            userSelectionsDTO = meetingVoteCacheService.getUserSelections(userId, meetingVoteId);
            if(userSelectionsDTO == null){
                userSelectionsDTO = getUserSelections(userId, meetingVoteId);
            }
        }
        VoteStatusDTO voteStatusDTO = meetingVoteCacheService.getVoteStatusInCache(meetingVoteId);
        if(voteStatusDTO == null){
            voteStatusDTO = meetingVoteCacheService.getVoteStatusInDB(meetingVoteId);
        }
        boolean voted = voteRepository.existsByCreatedByAndMeetingVote_Id(userId, meetingVoteId);
        return MeetingVoteStatusDTO.builder()
                .detail(detailDTO)
                .vote(userSelectionsDTO)
                .voted(voted)
                .voteStatus(voteStatusDTO)
                .build();
    }

    private UserSelectionsDTO getUserSelections(String userId, int meetingVoteId){
        UserSelectionsDTO res = null;
        Vote vote = voteRepository.findByCreatedByAndMeetingVote_Id(userId, meetingVoteId);
        if(vote != null){
            List<QuestionSelectDTO> selectDTO = vote.getSelectedOptions().stream()
                    .map(op -> new QuestionSelectDTO(
                            op.getQuestion().getId(),
                            op.getId()
                    )).toList();
            VoteDTO voteDTO = new VoteDTO(
                    null,
                    meetingVoteId,
                    selectDTO
            );
            res = meetingVoteCacheService.setUserSelections(userId, voteDTO);
        }
        return res;
    }

    private String truncate(String s){
        String[] w = s.split(" ");
        StringBuilder res =  new StringBuilder();
        for(int i = 0; i < Math.min(7, w.length); i++){
            res.append(w[i]).append(" ");
        }
        if(w.length > 7) res.append("...");
        return res.toString();
    }

}
