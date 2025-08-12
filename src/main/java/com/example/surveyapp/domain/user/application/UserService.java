package com.example.surveyapp.domain.user.application;

import com.example.surveyapp.domain.ai.moderation.config.ModerationResultStatusEnum;
import com.example.surveyapp.domain.ai.moderation.presentation.dto.NicknameModerationRequestDto;
import com.example.surveyapp.domain.ai.moderation.presentation.dto.NicknameModerationResponseDto;
import com.example.surveyapp.domain.ai.moderation.application.NicknameModerationService;
import com.example.surveyapp.domain.point.domain.model.entity.PointWallet;
import com.example.surveyapp.domain.point.domain.repository.PointRepository;
import com.example.surveyapp.domain.user.exception.UserErrorCode;
import com.example.surveyapp.domain.user.exception.UserException;
import com.example.surveyapp.domain.user.presentation.dto.*;
import com.example.surveyapp.domain.user.presentation.dto.RegisterRequestDto;
import com.example.surveyapp.domain.user.presentation.dto.UserRequestDto;
import com.example.surveyapp.domain.user.presentation.dto.UserResponseDto;
import com.example.surveyapp.domain.user.domain.model.CategoryEnum;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.model.UserBaseData;
import com.example.surveyapp.domain.user.domain.repository.UserBaseDataRepository;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PointRepository pointRepository;
    private final UserBaseDataRepository userBaseDataRepository;
    private final NicknameModerationService nicknameModerationService;

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
        validateNicknameModeration(requestDto.getNickname());

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = User.of(
                requestDto.getEmail(),
                encodedPassword,
                requestDto.getName(),
                requestDto.getNickname(),
                requestDto.getUserRole()
        );

        userRepository.save(user);

        PointWallet point = PointWallet.of(user.getId());
        pointRepository.save(point);
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto requestDto) {
        User user = userRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail())
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USER));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new UserException(UserErrorCode.NOT_MATCH_PASSWORD);
        }

        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getUserRole());

        return LoginResponseDto.builder()
                .id(String.valueOf(user.getId()))
                .name(user.getName())
                .token(accessToken)
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
    public UserResponseDto getMyInfo(Long userId){
        User user = findUser(userId);
        return UserResponseDto.from(user);
    }

    @Transactional
    public UserResponseDto updateMyInfo(Long userId, UserRequestDto requestDto){
        User user = findUser(userId);

        validateDuplicatedUser(requestDto);

        validateNicknameModeration(requestDto.getNickname());

        user.updateInfo(requestDto.getEmail(), requestDto.getName(), requestDto.getNickname(), requestDto.getPassword(), passwordEncoder);

        return UserResponseDto.from(user);
    }

    private void validateDuplicatedUser(UserRequestDto userRequestDto) {
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new UserException(UserErrorCode.EXISTS_EMAIL);
        }

        if (userRepository.existsByNickname(userRequestDto.getNickname())) {
            throw new UserException(UserErrorCode.EXISTS_NICKNAME);
        }
    }

    private void validateNicknameModeration(String nicknmae){
        NicknameModerationRequestDto nicknameModerationRequest = new NicknameModerationRequestDto(nicknmae);
        NicknameModerationResponseDto nicknameModerationResult = nicknameModerationService.moderate(nicknameModerationRequest);

        if(nicknameModerationResult.getStatus() == ModerationResultStatusEnum.DENIED){
            throw new UserException(UserErrorCode.INVALID_NICKNAME);
        }
    }

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
                    BaseDataResponseDto baseDataResponseDto = new BaseDataResponseDto(userBaseData.getCategory(), userBaseData.getData());
                    baseDataResponseDtoList.add(baseDataResponseDto);
                }
        );

        return new BaseDataListResponseDto(baseDataResponseDtoList);

    }

    // 참여자 기초 정보 가져오는 메서드
//    private UserBaseData getUserBaseData(CustomUserDetails user) {
//
//        user.getId();
//
//    }
}
