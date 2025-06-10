package org.vdt.qlch.voteservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.vdt.qlch.voteservice.dto.OptionVoteCount;
import org.vdt.qlch.voteservice.model.VoteSelect;

import java.util.List;

@Repository
public interface VoteSelectRepository extends JpaRepository<VoteSelect, Integer> {

    @Query(nativeQuery = true,
        value = """
                select option_id as optionId, cast(count(option_id) as int) as voteCount
                from vote_option_selects sl
                where option_id in ?1
                group by option_id""")
    List<OptionVoteCount> countVoteForOptions(List<Integer> optionsIds);

}
