package org.vdt.qlch.voteservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vdt.commonlib.dto.DocumentDTO;
import org.vdt.commonlib.dto.UserDTO;
import org.vdt.commonlib.exception.BadRequestException;
import org.vdt.commonlib.utils.AuthenticationUtil;
import org.vdt.qlch.voteservice.dto.OptionVoteCount;
import org.vdt.qlch.voteservice.dto.request.QuestionSelectDTO;
import org.vdt.qlch.voteservice.dto.request.VoteDTO;
import org.vdt.qlch.voteservice.dto.response.*;
import org.vdt.qlch.voteservice.model.*;
import org.vdt.qlch.voteservice.repository.MeetingVoteRepository;
import org.vdt.qlch.voteservice.repository.VoteRepository;
import org.vdt.qlch.voteservice.repository.VoteSelectRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingVoteCacheService {

    private final DocumentService documentService;

    private final MeetingVoteRepository meetingVoteRepository;

    private final VoteRepository voteRepository;

    private final CacheManager cacheManager;

    private final UserService userService;

    private final VoteSelectRepository voteSelectRepository;

    @Cacheable(value = "meeting-vote-list", key = "#meetingId")
    public List<MeetingVoteDTO> getList(int meetingId) {
        List<MeetingVote> meetingVoteList = meetingVoteRepository.findAllByMeetingIdOrderByStartTimeDesc(meetingId);
        return meetingVoteList.stream()
                .map(v -> {
                    boolean isVoted = voteRepository.existsByCreatedByAndMeetingVote_Id(
                            AuthenticationUtil.extractUserId(),
                            v.getId()
                    );
                    return new MeetingVoteDTO(
                            v.getId(),
                            v.getTitle(),
                            v.getDescription(),
                            v.getStartTime(),
                            v.getEndTime(),
                            v.getType(),
                            isVoted
                    );
                }).toList();
    }

    @Cacheable(value = "vote-detail-for-selection", key = "#meetingVoteId")
    public MeetingVoteDetailDTO getForSelection(int meetingVoteId) {
        MeetingVote meetingVote = getById(meetingVoteId);
        List<DocumentDTO> documents = documentService.getList(meetingVote.getDocuments().stream()
                .map(VoteDocument::getDocumentId).toList());
        List<QuestionDTO> questionDTOS = meetingVote.getQuestions().stream()
                .map(q -> {
                    List<OptionDTO> optionDTOS = q.getOptions().stream()
                            .map(o -> OptionDTO.builder()
                                    .id(o.getId())
                                    .content(o.getContent())
                                    .build()).toList();
                    return QuestionDTO.builder()
                            .id(q.getId())
                            .title(q.getTitle())
                            .options(optionDTOS)
                            .build();
                }).toList();
        return MeetingVoteDetailDTO.builder()
                .id(meetingVote.getId())
                .title(meetingVote.getTitle())
                .description(meetingVote.getDescription())
                .startTime(meetingVote.getStartTime())
                .endTime(meetingVote.getEndTime())
                .type(meetingVote.getType())
                .documents(documents)
                .questions(questionDTOS)
                .build();
    }

    @Cacheable(value = "user-vote-selection", key = "#userId+'-'+#voteDTO.meetingVoteId()")
    public UserSelectionsDTO setUserSelections(String userId, VoteDTO voteDTO) {
        return new UserSelectionsDTO(
                voteDTO.questionSelections().stream()
                        .map(qs ->
                                new QuestionSelectionDTO(qs.questionId(), qs.optionId())
                        ).toList()
        );
    }

    @Transactional
    @CachePut(value = "vote-status", key = "#meetingVoteId")
    public VoteStatusDTO updateVoteStatus(int meetingVoteId, List<QuestionSelectDTO> selections, UserDTO voter) {
        MeetingVote meetingVote = getById(meetingVoteId);
        VoteStatusDTO voteStatusDTO = getVoteStatusInCache(meetingVoteId);
        if(voteStatusDTO == null) voteStatusDTO = getVoteStatusInDB(meetingVoteId);
        voteStatusDTO.questions().forEach(q -> {
            QuestionSelectDTO qSel = selections.stream()
                    .dropWhile(s -> s.questionId() != q.questionId())
                    .toList().getFirst();
            q.options().forEach(o -> {
                if(o.getOptionId() == qSel.optionId()){
                    o.setVoteCount(o.getVoteCount() + 1);
                    if(voter != null){
                        List<VoterDTO> voters = new ArrayList<>(o.getVoterInfos());
                        voters.add(new VoterDTO(voter.fullName(), voter.picture()));
                        o.setVoterInfos(voters);
                    }
                }
            });
        });
        return voteStatusDTO;
    }

    @Cacheable(value = "vote-status", key = "#meetingVoteId")
    public VoteStatusDTO getVoteStatusInDB(int meetingVoteId){
        MeetingVote meetingVote = getById(meetingVoteId);
        List<Integer> optionsIds = new ArrayList<>();
        meetingVote.getQuestions().forEach(question -> optionsIds.addAll(question.getOptions().stream()
                .map(VoteOption::getId).toList()));
        List<QuestionStatusDTO> questionStatusDTOS;
        if(meetingVote.getType() == MeetingVoteType.PUBLIC){
            List<Vote> votes = voteRepository.findAllByMeetingVote_Id(meetingVoteId);
            List<String> userIds = votes.stream()
                    .map(Vote::getCreatedBy)
                    .toList();
            List<UserDTO> userDTOS = userService.getListByIds(userIds);
            Map<String, UserDTO> userMap = new HashMap<>();
            userDTOS.forEach(u -> userMap.put(u.id(), u));
            Map<Integer, OptionStatusDTO> voteCountMap = new HashMap<>();
            meetingVote.getQuestions().forEach(q -> q.getOptions().forEach(o -> voteCountMap.putIfAbsent(o.getId(), new OptionStatusDTO(o.getId(), 0, new ArrayList<>()))));
            votes.forEach(v -> v.getSelectedOptions().forEach(option -> {
                OptionStatusDTO os = voteCountMap.get(option.getId());
                os.setVoteCount(os.getVoteCount() + 1);
                os.getVoterInfos().add(VoterDTO.from(userMap.get(v.getCreatedBy())));
                voteCountMap.put(option.getId(), os);
            }));
            questionStatusDTOS = meetingVote.getQuestions().stream()
                    .map(q -> {
                        List<OptionStatusDTO> optionStatusDTOS = q.getOptions().stream()
                                .map(o -> voteCountMap.get(o.getId()))
                                .toList();
                        return new QuestionStatusDTO(
                                q.getId(),
                                optionStatusDTOS
                        );
                    })
                    .toList();
        }
        else{
            List<OptionVoteCount> voteCounts = voteSelectRepository.countVoteForOptions(optionsIds);
            Map<Integer, Integer> voteCountMap = new HashMap<>();
            voteCounts.forEach(voteCount ->
                    voteCountMap.put(voteCount.getOptionId(), voteCount.getVoteCount()));
            questionStatusDTOS = meetingVote.getQuestions().stream()
                    .map(q -> {
                        List<OptionStatusDTO> statusDTOS = q.getOptions().stream()
                                .map(o -> new OptionStatusDTO(
                                        o.getId(),
                                        voteCountMap.getOrDefault(o.getId(), 0),
                                        new ArrayList<>()
                                )).toList();
                        return new QuestionStatusDTO(
                                q.getId(),
                                statusDTOS
                        );
                    }).toList();
        }
        return VoteStatusDTO.builder()
                .meetingVoteId(meetingVote.getId())
                .questions(questionStatusDTOS)
                .build();
    }

    public VoteStatusDTO getVoteStatusInCache(int meetingVoteId) {
        VoteStatusDTO res = null;
        try{
            Cache cache = cacheManager.getCache("vote-status");
            if(cache != null){
                Cache.ValueWrapper wrapper = cache.get(meetingVoteId);
                if(wrapper != null){
                    res = (VoteStatusDTO) wrapper.get();
                }
            }
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
        return res;
    }

    public UserSelectionsDTO getUserSelections(String userId, int meetingVoteId){
        UserSelectionsDTO res = null;
        try{
            Cache cache = cacheManager.getCache("user-vote-selection");
            if(cache != null){
                Cache.ValueWrapper wrapper = cache.get(String.format("%s-%s", userId, meetingVoteId));
                if(wrapper != null){
                    res = (UserSelectionsDTO) wrapper.get();
                }
            }
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
        return res;
    }

    public MeetingVote getById(int id){
        MeetingVote meetingVote = meetingVoteRepository.findById(id);
        if(meetingVote == null){
            throw new BadRequestException(org.vdt.qlch.voteservice.utils.Constants.ErrorCode.MEETING_VOTE_NOT_FOUND);
        }
        return meetingVote;
    }

}
