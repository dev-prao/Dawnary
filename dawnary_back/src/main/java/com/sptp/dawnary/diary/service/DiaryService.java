package com.sptp.dawnary.diary.service;

import com.sptp.dawnary.diary.domain.Diary;
import com.sptp.dawnary.diary.dto.DiaryResponse;
import com.sptp.dawnary.diary.dto.DiaryRequest;
import com.sptp.dawnary.diary.repository.DiaryRepository;
import com.sptp.dawnary.global.exception.DiaryNotFoundException;
import com.sptp.dawnary.global.exception.MemberNotFoundException;
import com.sptp.dawnary.global.exception.RegistFailedException;
import com.sptp.dawnary.global.util.GcpUtil;
import com.sptp.dawnary.global.util.MemberInfo;
import com.sptp.dawnary.member.domain.Member;
import com.sptp.dawnary.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final GcpUtil gcpUtil;

    public DiaryResponse saveDiary(DiaryRequest diaryRequest) {
        Diary diary = Diary.toEntity(diaryRequest);
        diary.setMember(getMember());
        diary.setSentiment(gcpUtil.getSentiment(diaryRequest.content()));
        diaryRepository.save(diary);
        validateSave(diary);
        return DiaryResponse.toResponse(diary);
    }

    public DiaryResponse updateDiary(Long diaryId, DiaryRequest diaryRequest) {
        validateExistence(diaryId);
        Diary diary = diaryRepository.findById(diaryId).get();
        if(diary.getMember().getId() != MemberInfo.getMemberId()) throw new DiaryNotFoundException();
        diary.setTitle(diaryRequest.title());
        diary.setContent(diaryRequest.content());
        diary.setSentiment(gcpUtil.getSentiment(diaryRequest.content()));
        diaryRepository.save(diary);
        validateSave(diary);
        return DiaryResponse.toResponse(diary);
    }

    public boolean deleteDiary(Long diaryId) {
        if (diaryRepository.existsById(diaryId)) {
            diaryRepository.deleteById(diaryId);
            return true;
        }
        return false;
    }

    public List<DiaryResponse> findAllDiaries(Long memberId) {
        if (isLoggedInUser(memberId)) {
            return diaryRepository.findByMemberId(memberId)
                    .stream()
                    .map(DiaryResponse::toResponse)
                    .toList();
        }
        return diaryRepository.findOtherUserDiaries(memberId)
                .stream()
                .map(DiaryResponse::toResponse)
                .toList();
    }

    public DiaryResponse findDiary(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(DiaryNotFoundException::new);
        return DiaryResponse.toResponse(diary);
    }

    public List<DiaryResponse> findFollowDiary() {
        return diaryRepository.findFollowUserDiaries(MemberInfo.getMemberId())
                .stream()
                .map(DiaryResponse::toResponse)
                .toList();
    }



    private Member getMember() {
        return memberRepository.findById(MemberInfo.getMemberId())
                .orElseThrow(MemberNotFoundException::new);
    }

    private void validateSave(Diary diary) {
        if (diary == null) {
            throw new RegistFailedException();
        }
    }

    private void validateExistence(Long diaryId) {
        if (!diaryRepository.existsById(diaryId)) {
            throw new DiaryNotFoundException();
        }
    }

    private boolean isLoggedInUser(Long memberId) {
        return getMember().getId().equals(memberId);
    }
}
