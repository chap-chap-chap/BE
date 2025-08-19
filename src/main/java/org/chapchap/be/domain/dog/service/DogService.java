// package org.chapchap.be.domain.dog.service;
package org.chapchap.be.domain.dog.service;

import lombok.RequiredArgsConstructor;
import org.chapchap.be.domain.dog.dto.DogProfileRequest;
import org.chapchap.be.domain.dog.entity.Dog;
import org.chapchap.be.domain.user.entity.User;
import org.chapchap.be.domain.dog.repository.DogRepository;
import org.chapchap.be.domain.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DogService {

    private final DogRepository dogRepository;
    private final UserService userService;

    // 요청한 강아지 프로필들을 등록하고, 최종 등록 개수를 반환
    @Transactional
    public int registerDogsForUserEmail(String ownerEmail, DogProfileRequest req) {
        User owner = userService.getByEmail(ownerEmail);

        int savedCount = 0;
        for (var d : req.dogs()) {
            // 필요 시 이름 중복 방지
            if (dogRepository.existsByOwnerIdAndName(owner.getId(), d.name())) {
                throw new IllegalArgumentException("이미 등록된 강아지 이름입니다: " + d.name());
            }

            dogRepository.save(Dog.builder()
                    .owner(owner)
                    .name(d.name())
                    .breed(d.breed())
                    .weightKg(d.weightKg())
                    .ageMonths(d.ageMonths())
                    .archived(false)
                    .build());
            savedCount++;
        }
        return savedCount;
    }
}
