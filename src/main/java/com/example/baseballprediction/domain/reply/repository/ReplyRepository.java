package com.example.baseballprediction.domain.reply.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.baseballprediction.domain.game.dto.GameReplyDTO;
import com.example.baseballprediction.domain.reply.entity.Reply;
import com.example.baseballprediction.global.constant.ReplyType;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
	Page<Reply> findByType(ReplyType type, Pageable pageable);

	@Query(
			"SELECT "
					+ " new com.example.baseballprediction.domain.game.dto.GameReplyDTO(r.id ,"
					+ " count(rl.id) as count,"
					+ " r.createdAt,"
					+ " r.content,"
					+ " m.profileImageUrl,"
					+ " m.nickname,"
					+ " t.name)"
					+ " from reply r "
					+ " LEFT JOIN reply_like rl"
					+ " ON rl.reply.id = r.id "
					+ " LEFT JOIN Member m "
					+ "	ON m.id = r.member.id"
					+ "	LEFT JOIN team t"
					+ "	ON t.id = m.team.id"
					+ "	WHERE r.type =:replyType"
					+ "	AND date(r.createdAt) = date(now())"
					+ " group by r.id"
					+ "	order by  r.createdAt desc")
	Page<GameReplyDTO> findReplyGame(@Param("replyType")ReplyType replyType,Pageable pageable);
}
