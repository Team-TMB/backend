package com.example.baseballprediction.domain.game.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.baseballprediction.domain.game.dto.GameResponse;
import com.example.baseballprediction.domain.game.dto.GameResponse.GameDtoDaily;
import com.example.baseballprediction.domain.game.dto.GameVoteProjection;
import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.game.repository.GameRepository;
import com.example.baseballprediction.domain.gamevote.dto.GameVoteRatioDTO;
import com.example.baseballprediction.domain.gamevote.repository.GameVoteRepository;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.error.exception.NotFoundException;
import com.example.baseballprediction.global.util.CustomDateUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameService {

	private final GameRepository gameRepository;
	private final GameVoteRepository gameVoteRepository;
	private final MemberRepository memberRepository;

	public List<GameDtoDaily> findDailyGame(Long memberId) {
		List<Game> games = gameRepository.findAll();

		List<GameDtoDaily> gameDTOList = new ArrayList<>();

		for (Game game : games) {
			String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			String gameFormatDate = game.getStartedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

			if (gameFormatDate.equals(formatDate)) {
				GameVoteRatioDTO gameVoteRatioDTO = gameVoteRepository.findVoteRatio(game.getHomeTeam().getId(),
					game.getAwayTeam().getId(), game.getId()).orElseThrow();
				
				boolean homeTeamHasVoted = false;
				boolean awayTeamHasVoted = false;
				
				if(memberId != null) {
					homeTeamHasVoted = gameVoteRepository.existsByGameIdAndTeamIdAndMemberId(game.getId(), game.getHomeTeam().getId(), memberId);
					awayTeamHasVoted = gameVoteRepository.existsByGameIdAndTeamIdAndMemberId(game.getId(), game.getAwayTeam().getId(), memberId);
				}

				GameDtoDaily dailygame = new GameDtoDaily(game, game.getHomeTeam(), game.getAwayTeam(),
					gameVoteRatioDTO,homeTeamHasVoted, awayTeamHasVoted);

				gameDTOList.add(dailygame);

			}

		}

		return gameDTOList;

	}

	public List<GameResponse.PastGameDTO> findGameResult(String username, String startDate, String endDate) {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		LocalDateTime startDateTime = LocalDateTime.of(CustomDateUtil.stringToDate(startDate), LocalTime.of(0, 0));
		LocalDateTime endDateTime = LocalDateTime.of(CustomDateUtil.stringToDate(endDate), LocalTime.of(23, 59));

		List<GameVoteProjection> gameVoteProjections = gameRepository.findPastGameByStartedAtBetween(member.getId(),
			startDateTime, endDateTime);

		List<GameResponse.PastGameDTO> gameResults = new ArrayList<>();

		for (GameVoteProjection gameVoteProjection : gameVoteProjections) {
			Integer homeTeamId = gameVoteProjection.getHomeTeamId();
			Integer awayTeamId = gameVoteProjection.getAwayTeamId();
			GameVoteRatioDTO gameVoteRatioDTO = gameVoteRepository.findVoteRatio(gameVoteProjection.getHomeTeamId(),
				gameVoteProjection.getAwayTeamId(), gameVoteProjection.getGameId()).orElseThrow();

			gameResults.add(new GameResponse.PastGameDTO(gameVoteProjection, gameVoteRatioDTO));
		}

		return gameResults;
	}

}