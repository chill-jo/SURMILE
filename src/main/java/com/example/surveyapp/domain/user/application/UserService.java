package com.example.surveyapp.domain.user.application;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.example.surveyapp.domain.surveyanswer.exception.AnswerErrorCode;
import com.example.surveyapp.domain.surveyanswer.exception.AnswerException;
import com.example.surveyapp.domain.user.application.facade.UserModerationFacade;
import com.example.surveyapp.domain.user.domain.model.UserOutbox;
import com.example.surveyapp.domain.user.domain.repository.UserOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapp.domain.user.application.provider.JwtProvider;
import com.example.surveyapp.domain.user.domain.event.RegisterEvent;
import com.example.surveyapp.domain.user.domain.model.CategoryEnum;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.model.UserBaseData;
import com.example.surveyapp.domain.user.domain.repository.UserBaseDataRepository;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.domain.user.exception.UserErrorCode;
import com.example.surveyapp.domain.user.exception.UserException;
import com.example.surveyapp.domain.user.presentation.dto.BaseDataInfoResponseDto;
import com.example.surveyapp.domain.user.presentation.dto.BaseDataListRequestDto;
import com.example.surveyapp.domain.user.presentation.dto.BaseDataListResponseDto;
import com.example.surveyapp.domain.user.presentation.dto.BaseDataResponseDto;
import com.example.surveyapp.domain.user.presentation.dto.LoginRequestDto;
import com.example.surveyapp.domain.user.presentation.dto.LoginResponseDto;
import com.example.surveyapp.domain.user.presentation.dto.RegisterRequestDto;
import com.example.surveyapp.domain.user.presentation.dto.UserRequestDto;
import com.example.surveyapp.domain.user.presentation.dto.UserResponseDto;
import com.example.surveyapp.domain.user.presentation.dto.WithdrawRequestDto;
import com.example.surveyapp.global.redis.infrastructure.RedisTemplate;
import com.example.surveyapp.global.response.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserBaseDataRepository userBaseDataRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final RedisTemplate redisTemplate;
	private final UserModerationFacade userModerationFacade;
	private final JwtProvider jwtProvider;
	private final ObjectMapper objectMapper;
	private final UserOutboxRepository userOutboxRepository;

	@Value("${spring.data.redis.cache-access-token}")
	private String ACCESS_TOKEN;
	@Value("${jwt.access-token.expiration.access-token}")
	private long ACCESS_TOKEN_TIME;

	@Value("${spring.data.redis.cache-refresh-token}")
	private String REFRESH_TOKEN;
	@Value("${jwt.access-token.expiration.refresh-token}")
	private long REFRESH_TOKEN_TIME;

	@Transactional
	public void register(RegisterRequestDto requestDto) {
		if (userRepository.existsByEmail(requestDto.getEmail())) {
			throw new UserException(UserErrorCode.EXISTS_EMAIL);
		}

		if (userRepository.existsByNickname(requestDto.getNickname())) {
			throw new UserException(UserErrorCode.EXISTS_NICKNAME);
		}

		if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
			throw new UserException(UserErrorCode.NOT_MATCH_PASSWORD);
		}
		userModerationFacade.checkNicknameModeration(null, null, requestDto.getNickname());

		String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

		User user = User.of(
			requestDto.getEmail(),
			encodedPassword,
			requestDto.getName(),
			requestDto.getNickname(),
			requestDto.getUserRole()
		);

		userRepository.save(user);

		RegisterEvent event = new RegisterEvent(user.getId());

		try {
			String payload = objectMapper.writeValueAsString(event);
			UserOutbox userOutbox = UserOutbox.of("User",
					user.getId(),
					payload);

			userOutboxRepository.save(userOutbox);

		} catch (JsonProcessingException e) {
			throw new AnswerException(AnswerErrorCode.CANNOT_CONVERT_PAYLOAD);
		}
	}

	@Transactional(readOnly = true)
	public LoginResponseDto login(LoginRequestDto requestDto) {
		User user = userRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail())
			.orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USER));

		if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
			throw new UserException(UserErrorCode.NOT_MATCH_PASSWORD);
		}

		String accessToken = jwtProvider.createAccessToken(user.getId(), user.getUserRole());
		String refreshToken = jwtProvider.createRefreshToken(user.getId());
		redisTemplate.write(REFRESH_TOKEN + ":" + user.getId(), jwtProvider.substringToken(refreshToken),
			Duration.ofMillis(REFRESH_TOKEN_TIME));

		return LoginResponseDto.builder()
			.id(String.valueOf(user.getId()))
			.name(user.getName())
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	@Transactional
	public void logout(String accessToken) {
		Long userId = Long.parseLong(jwtProvider.extractUserId(jwtProvider.substringToken(accessToken)));
		redisTemplate.write(ACCESS_TOKEN + ":" + userId, jwtProvider.substringToken(accessToken),
			Duration.ofMillis(ACCESS_TOKEN_TIME));
	}

	@Transactional
	public LoginResponseDto refresh(String requestToken) {
		String token = jwtProvider.substringToken(requestToken);

		if (!jwtProvider.isValidRefreshToken(token)) {
			throw new UnauthorizedException("유효하지 않은 JWT 토큰입니다.");
		}

		Long userId = Long.parseLong(jwtProvider.extractUserId(token));

		User user = userRepository.findByIdAndIsDeletedFalse(userId)
			.orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USER));

		String accessToken = jwtProvider.createAccessToken(userId, user.getUserRole());
		String refreshToken = jwtProvider.createRefreshToken(userId);
		redisTemplate.write(REFRESH_TOKEN + ":" + userId, jwtProvider.substringToken(refreshToken),
			Duration.ofMillis(REFRESH_TOKEN_TIME));
		;

		return LoginResponseDto.builder()
			.id(String.valueOf(user.getId()))
			.name(user.getName())
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();

	}

	@Transactional
	public void withdraw(Long userId, WithdrawRequestDto requestDto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USER));

		if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
			throw new UserException(UserErrorCode.NOT_MATCH_PASSWORD);
		}

		user.softDelete();
	}

	@Transactional(readOnly = true)
	public UserResponseDto getMyInfo(Long userId) {
		User user = findUser(userId);
		return UserResponseDto.from(user);
	}

	@Transactional
	public UserResponseDto updateMyInfo(Long userId, UserRequestDto requestDto) {
		User user = findUser(userId);

		validateDuplicatedUser(requestDto);

		userModerationFacade.checkNicknameModeration(userId, requestDto.getEmail(), requestDto.getNickname());

		user.updateInfo(requestDto.getEmail(), requestDto.getName(), requestDto.getNickname(), requestDto.getPassword(),
			passwordEncoder);

		return UserResponseDto.from(user);
	}

	// 유저 정보 중복 여부 검사
	private void validateDuplicatedUser(UserRequestDto userRequestDto) {
		if (userRepository.existsByEmail(userRequestDto.getEmail())) {
			throw new UserException(UserErrorCode.EXISTS_EMAIL);
		}

		if (userRepository.existsByNickname(userRequestDto.getNickname())) {
			throw new UserException(UserErrorCode.EXISTS_NICKNAME);
		}
	}

	// ID로 유저 찾기
	private User findUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USER));
	}

	// 참여자 로그인 시 기초 정보 체크하여 없을 경우 기초정보 입력 메서드 호출
	// 참여자 기초 정보 작성 선택지 보여주는 메서드
	@Transactional(readOnly = true)
	public BaseDataInfoResponseDto getBaseDataInfo() {

		return new BaseDataInfoResponseDto();
	}

	// 참여자 기초 정보 C,U
	@Transactional
	public void saveBaseDatas(Long userId, BaseDataListRequestDto requestDto) {

		User user = userRepository.findById(userId).orElseThrow(
			() -> new UserException(UserErrorCode.NOT_FOUND_USER)
		);

		if (CategoryEnum.values().length != requestDto.getList().size()) {
			throw new UserException(UserErrorCode.MISSING_BASE_DATA_CATEGORIES);
		}

		requestDto.getList().forEach(
			item -> {
				UserBaseData existingData = userBaseDataRepository.findByUserIdAndCategory(user, item.getCategory())
					.orElse(null);

				if (existingData != null) {
					existingData.update(item.getAnswer());
				} else {
					UserBaseData userBaseData = UserBaseData.of(user, item.getCategory(), item.getAnswer());
					userBaseDataRepository.save(userBaseData);
				}
			}
		);

	}

	// 참여자 기초 정보 R
	@Transactional(readOnly = true)
	public BaseDataListResponseDto getBaseDatas(Long userId) {

		User user = userRepository.findById(userId).orElseThrow(
			() -> new UserException(UserErrorCode.NOT_FOUND_USER)
		);

		List<UserBaseData> userBaseDataList = userBaseDataRepository.findAllByUserId(user);
		List<BaseDataResponseDto> baseDataResponseDtoList = new ArrayList<>();
		userBaseDataList.forEach(
			userBaseData -> {
				BaseDataResponseDto baseDataResponseDto = new BaseDataResponseDto(userBaseData.getCategory(),
					userBaseData.getData());
				baseDataResponseDtoList.add(baseDataResponseDto);
			}
		);

		return new BaseDataListResponseDto(baseDataResponseDtoList);

	}
}
