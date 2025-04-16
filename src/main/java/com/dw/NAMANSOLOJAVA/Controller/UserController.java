package com.dw.NAMANSOLOJAVA.Controller;

import com.dw.NAMANSOLOJAVA.DTO.*;
import com.dw.NAMANSOLOJAVA.Repository.MediaRepository;
import com.dw.NAMANSOLOJAVA.Service.UserService;
import com.dw.NAMANSOLOJAVA.model.Media;
import com.dw.NAMANSOLOJAVA.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    MediaRepository mediaRepository;

    @PostMapping("/register") // 회원가입
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
        System.out.println(userDTO.getBirthM());
        System.out.println(userDTO.getUsername());
        return new ResponseEntity<>(
                userService.registerUser(userDTO),
                HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return new ResponseEntity<>(
                userService.getAllUsers(),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/current-user")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return new ResponseEntity<>(userService.getCurrentUser().toUserDTO(), HttpStatus.OK);
    }
//    @GetMapping("/id/{id}")
//    public ResponseEntity<UserDTO> getUserById(@PathVariable String username){
//        return new ResponseEntity<>(userService.getUserById(username),HttpStatus.OK);
//    }

    @GetMapping("/check-id/{username}")
    public ResponseEntity<Boolean> checkId(@PathVariable String username){
        return new ResponseEntity<>(userService.checkId(username),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all/add-date")
    public ResponseEntity<List<UserAddDateDTO>> getAllUsersAddDate(){
        return new ResponseEntity<>(
                userService.getAllUsersAddDate(),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/id/{username}")
    public ResponseEntity<UserAddDateDTO> getUserByIdAdim(@PathVariable String username) {
        return new ResponseEntity<>(
                userService.getUserByIdAdmin(username),
                HttpStatus.OK);
    }

    @GetMapping("/find-user/email")
    public ResponseEntity<String> getIdByEmail(@RequestParam String realName, @RequestParam String email) {
        return new ResponseEntity<>(userService.getIdByEmail(realName,email), HttpStatus.OK);
    }

    @GetMapping("/find-user/phone")
    public ResponseEntity<String> getIdByPhone(@RequestParam String realName, @RequestParam String phoneNumber) {
        return new ResponseEntity<>(
                userService.getIdByPhone(realName,phoneNumber),
                HttpStatus.OK);
    }

    @GetMapping("/exist")
    public ResponseEntity<Boolean> ExistEmailAndUsername(@RequestParam String username, @RequestParam String realName, @RequestParam String email){
        return new ResponseEntity<>(
                userService.ExistEmailAndUsername(username,realName,email),
                HttpStatus.OK);
    }


    @PutMapping("/modify-pw")
    public ResponseEntity<String> UpdatePw(@RequestBody PasswordDTO passwordDTO) {
        return new ResponseEntity<>(
                userService.UpdatePw(passwordDTO),
                HttpStatus.OK);
    }

    
    @PutMapping("/user-data")
    public ResponseEntity<UserUpdateAndFIndDTO> UpdateUserData(@RequestBody UserUpdateAndFIndDTO userUpdateAndFIndDTO) {
        return new ResponseEntity<>(
                userService.UpdateUserData(userUpdateAndFIndDTO),
                HttpStatus.OK);
    }

    @PutMapping("/user-data-image-dday")
    public ResponseEntity<UpdateImageDDayDTO> UpdateUserDataImageDday(@RequestBody UpdateImageDDayDTO updateImageDDayDTO) {
        return new ResponseEntity<>(
                userService.UpdateUserDataImageDday(updateImageDDayDTO),
                HttpStatus.OK);
    }

    @PutMapping("/user/alarm-setting")
    public ResponseEntity<UserAlarmSettingDTO> AlarmSetting(@RequestBody UserAlarmSettingDTO userAlarmSettingDTO) {
        return new ResponseEntity<>(
                userService.AlarmSetting(userAlarmSettingDTO),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/monthly/album-count")
    public ResponseEntity<List<MonthlyUserAlbumCountDTO>> getMonthlyAlbumCountForAllUsers() {
        return ResponseEntity.ok(userService.monthlyAlbumCountForAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/last-activity")
    public ResponseEntity<List<UserLastActivityDTO>> getUserLastActivity() {
        return new ResponseEntity<>(
                userService.getUserLastActivity(),
                HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUserInfo() {
        User currentUser = userService.getCurrentUser();
        return new ResponseEntity<>(new UserDTO(currentUser), HttpStatus.OK);
    }

    @PostMapping("/upload/profile-image")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        User user = userService.getCurrentUser();
        String username = user.getUsername();
        String uploadDir = "./var/upload/" + username;

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 확장자 추출
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 사용자명으로 고정된 파일명 설정
        String newFileName = username + ext;
        Path savePath = Paths.get(uploadDir, newFileName);

        MediaDTO mediaDTO = new MediaDTO();

        try {
            Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);

            mediaDTO.setMediaUrl("/api/user/download/" + username + "/" + newFileName);
            mediaDTO.setMediaType(file.getContentType().startsWith("video") ? "VIDEO" : "PICTURE");

            return new ResponseEntity(mediaDTO, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("업로드 중 오류: " + e.getMessage());
        }
    }

    // 프로필 이미지 다운로드
    @GetMapping("/download/{username}/{fileName}")
    public ResponseEntity<Resource> downloadProfileImage(
            @PathVariable String username,
            @PathVariable String fileName) {
        try {
            Path basePath = Paths.get("./var/upload").resolve(username).normalize();
            Path filePath = basePath.resolve(fileName).normalize();

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
