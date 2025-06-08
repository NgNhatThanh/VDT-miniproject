package org.vdt.qlch.voteservice.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vdt.commonlib.dto.MeetingHistoryMessage;
import org.vdt.commonlib.exception.BadRequestException;
import org.vdt.commonlib.model.MeetingHistoryType;
import org.vdt.commonlib.utils.AuthenticationUtil;
import org.vdt.commonlib.utils.Constants;
import org.vdt.commonlib.utils.ScheduleUtil;
import org.vdt.qlch.voteservice.dto.CreateVoteDTO;
import org.vdt.qlch.voteservice.dto.MeetingVoteDTO;
import org.vdt.qlch.voteservice.model.*;
import org.vdt.qlch.voteservice.producer.MeetingHistoryProducer;
import org.vdt.qlch.voteservice.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

    @Transactional
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
        MeetingVote finalMeetingVote = meetingVoteRepository.save(meetingVote);
        List<VoteOption> options = new ArrayList<>();
        List<VoteQuestion> questions = dto.questions().stream()
                .map(q -> {
                    VoteQuestion question = new VoteQuestion();
                    question.setVote(finalMeetingVote);
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
                        .vote(meetingVote)
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

    public List<MeetingVoteDTO> getList(int meetingId) {
        List<MeetingVote> meetingVoteList = meetingVoteRepository.findAllByMeetingIdOrderByStartTimeDesc(meetingId);
        return meetingVoteList.stream()
                .map(v -> {
                    boolean isVoted = voteRepository.existsByCreatedByAndMeetingVote(
                            AuthenticationUtil.extractUserId(),
                            v
                    );
                    return new MeetingVoteDTO(
                            v.getTitle(),
                            v.getDescription(),
                            v.getStartTime(),
                            v.getEndTime(),
                            isVoted
                    );
                }).toList();
    }
}
