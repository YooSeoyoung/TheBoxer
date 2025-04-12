package com.dw.NAMANSOLOJAVA.Service;

import com.dw.NAMANSOLOJAVA.DTO.*;
import com.dw.NAMANSOLOJAVA.Exception.InvalidRequestException;
import com.dw.NAMANSOLOJAVA.Exception.ResourceNotFoundException;
import com.dw.NAMANSOLOJAVA.Repository.MediaRepository;
import com.dw.NAMANSOLOJAVA.Repository.OfficialEventRepository;
import com.dw.NAMANSOLOJAVA.Repository.ToDoRepository;
import com.dw.NAMANSOLOJAVA.enums.MediaType;
import com.dw.NAMANSOLOJAVA.model.Media;
import com.dw.NAMANSOLOJAVA.model.ToDo;
import com.dw.NAMANSOLOJAVA.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ToDoService {
    @Autowired
    ToDoRepository toDoRepository;

    @Autowired
    OfficialEventRepository eventRepository;

    @Autowired
    UserService userService;

    @Autowired
    MediaRepository mediaRepository;



//    private String getCurrentUsername() {
//        return SecurityContextHolder.getContext().getAuthentication().getName();
//    }

    public ToDoAllDTO getAllTodo() {
        User user = userService.getCurrentUser();
        List<ToDo> anniversaryList = toDoRepository.findAllByUsernameAndType(user.getUsername(), "ANNIVERSARY");
        List<ToDo> toTravelList = toDoRepository.findAllByUsernameAndType(user.getUsername(), "TRAVEL");
        List<AnniversaryDTO> anverDTOs = anniversaryList.stream().map(ToDo::toAnniDTO).toList();
        List<ToDoTravelDTO> travelDTOs = toTravelList.stream().map(ToDo::toTravelDTO).toList();
        return new ToDoAllDTO(travelDTOs, anverDTOs);
    }

    public List<AnniversaryDTO> getAllAnniversary() {
//            String username = getCurrentUsername();
        User user = userService.getCurrentUser();
        List<ToDo> anniversaryList = toDoRepository.findAllByUsernameAndType(user.getUsername(), "ANNIVERSARY");
        if (anniversaryList.isEmpty()) {
            throw new ResourceNotFoundException("작성된 기념일이 없습니다.");
        }
        return anniversaryList.stream().map(ToDo::toAnniDTO).toList();
    }

    public List<ToDoTravelDTO> getAllTravel() {
//            String username = getCurrentUsername();

        User user = userService.getCurrentUser();

        List<ToDo> toTravelList = toDoRepository.findAllByUsernameAndType(user.getUsername(), "TRAVEL");
        return toTravelList.stream().map(ToDo::toTravelDTO).toList();
    }

    public AnniversaryDTO saveAnniversary(AnniversaryDTO dto) {
//        String username = getCurrentUsername();
        User user = userService.getCurrentUser();

        ToDo todo = new ToDo();

        todo.setTitle(dto.getTitle());
        todo.setStartDate(dto.getStartDate());
        todo.setLastDate(dto.getStartDate()); // 기념일은 시작일 = 종료일
        todo.setFinalEditDate(LocalDate.now());
        todo.setType(dto.getType());
        todo.setUser(user);
        todo.setEditable(true);
        todo.setMedia(new ArrayList<>());
        todo.setColor(dto.getColor());

        ToDo saved = toDoRepository.save(todo);
        return saved.toAnniDTO();
    }

    public ToDoTravelDTO saveTravel(ToDoTravelDTO dto, TravelMediaDTO mediaDTO) throws IOException {
//        String username = getCurrentUsername();
        User user = userService.getCurrentUser();
        List<Media> mediaList = new ArrayList<>();

        for (MultipartFile file : mediaDTO.getMultipartFiles()) {
            if (!file.isEmpty()) {
                String originalName = file.getOriginalFilename(); // 원래 파일명
                String storedName = System.currentTimeMillis() + "_" + originalName; // 시간을 읽어오고 이름과 같이 저장함

                Path userDir = Paths.get("uploads").resolve(user.getUsername()); // 저장 경로
                Files.createDirectories(userDir); // 없으면 생성

                Path filePath = userDir.resolve(storedName); // 저장할 실제 파일 경로 만들기

                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING); // 파일 저장

                String filePathForClient = "/uploads/" + user.getUsername() + "/" + storedName; // DB에 저장할 경로 만들기

                Media media = new Media();
                media.setMediaUrl(filePathForClient);
                media.setMediaType(MediaType.valueOf(file.getContentType()));

                mediaList.add(mediaRepository.save(media));
            }
        }

        ToDo todo = new ToDo();

        todo.setTitle(dto.getTitle());
        todo.setStartDate(dto.getStartDate());
        todo.setLastDate(dto.getLastDate());
        todo.setFinalEditDate(LocalDate.now());
        todo.setType(dto.getType());
        todo.setUser(user);
        todo.setEditable(true);
        todo.setColor(dto.getColor());
        todo.setMedia(mediaList);

        ToDo saved = toDoRepository.save(todo);

        return saved.toTravelDTO();
    }

    public AnniversaryDTO getAnniversaryById(Long id) {
//        String username = getCurrentUsername();
        User user = userService.getCurrentUser();
        ToDo todo = toDoRepository.findByIdAndUsername(id, user.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("해당 기념일은 등록되어 있지 않습니다."));
        if (!todo.getType().equalsIgnoreCase("ANNIVERSARY")) {
            throw new ResourceNotFoundException("해당 기념일 항목이 없습니다.");
        }
        return todo.toAnniDTO();
    }

    public ToDoTravelDTO getToDoTravelById(Long id) {
//        String username = getCurrentUsername();
        User user = userService.getCurrentUser();
        ToDo todo = toDoRepository.findByIdAndUsername(id, user.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("해당 여행 일정은 등록되어 있지 않습니다."));
        if (!todo.getType().equalsIgnoreCase("TRAVEL")) {
            throw new ResourceNotFoundException("해당 여행 일정이 없습니다.");
        }
        return todo.toTravelDTO();
    }

    @Transactional
    public ToDoTravelDTO updateToDoTravelById(Long id, ToDoTravelDTO dto, TravelMediaDTO mediaDTO) throws IOException {
//        String username = getCurrentUsername();
        User user = userService.getCurrentUser();
        ToDo todo = toDoRepository.findByIdAndUsername(id, user.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("해당 여행 일정은 등록되어 있지 않습니다."));

        if (!todo.getType().equals("TRAVEL")) {
            throw new InvalidRequestException("해당 항목은 여행 일정이 아닙니다.");
        }

        todo.setUser(user);
        todo.setTitle(dto.getTitle());
        todo.setStartDate(dto.getStartDate());
        todo.setLastDate(dto.getLastDate());
        todo.setFinalEditDate(LocalDate.now());

        // 기존 미디어 처리
        List<Media> originMedia = todo.getMedia();
        List<Long> incomingMediaIds = dto.getMediaUrl().stream()
                .map(MediaDTO::getId)
                .filter(Objects::nonNull)
                .toList();

        List<Media> deleteMedia = originMedia.stream()
                .filter(m -> !incomingMediaIds.contains(m.getId()))
                .toList();

        originMedia.removeAll(deleteMedia);

        // 새 이미지 파일 저장
        List<Media> newMedias = new ArrayList<>();
        if (mediaDTO.getMultipartFiles() != null) {
            Path uploadPath = Paths.get("uploads")
                    .resolve(user.getUsername());
            Files.createDirectories(uploadPath);

            for (MultipartFile file : mediaDTO.getMultipartFiles()) {
                if (!file.isEmpty()) {
                    String originalName = file.getOriginalFilename();
                    String storedName = System.currentTimeMillis() + "_" + originalName;
                    Path filePath = uploadPath.resolve(storedName);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    String fileUrl = "/uploads/" + user.getUsername() + "/" + storedName;

                    Media media = new Media();
                    media.setMediaUrl(fileUrl);
                    media.setMediaType(MediaType.valueOf(file.getContentType()));
                    newMedias.add(mediaRepository.save(media));
                }
            }
        }

        originMedia.addAll(newMedias);
        todo.setMedia(originMedia);
        todo.setColor(dto.getColor());
        todo.setType(dto.getType());

        ToDo updated = toDoRepository.save(todo);
        return updated.toTravelDTO();
    }

    @Transactional
    public AnniversaryDTO updateAnniversaryById(Long id, AnniversaryDTO dto) {
//        String username = getCurrentUsername();
        User user = userService.getCurrentUser();
        ToDo todo = toDoRepository.findByIdAndUsername(id, user.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("해당 기념일은 등록되어 있지 않습니다."));

        if (!"ANNIVERSARY".equalsIgnoreCase(todo.getType())) {
            throw new InvalidRequestException("해당 항목은 기념일이 아닙니다.");
        }

        todo.setTitle(dto.getTitle());
        todo.setStartDate(dto.getStartDate());
        todo.setLastDate(dto.getStartDate()); // 기념일은 시작일이 곧 일정의 끝
        todo.setFinalEditDate(LocalDate.now()); // 미디어는 이미 생성 시점에 new ArrayList() 했음.
        todo.setColor(dto.getColor());
        todo.setType(dto.getType());

        ToDo updated = toDoRepository.save(todo);
        return updated.toAnniDTO();
    }

    @Transactional
    public String deleteAnniversaryById(Long id) {
//        String username = getCurrentUsername();
        User user = userService.getCurrentUser();
        ToDo todo = toDoRepository.findByIdAndUsername(id, user.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("해당 기념일은 등록되어 있지 않습니다."));

        if (!todo.getEditable()) {
            throw new InvalidRequestException("비정상적인 접근입니다.");
        }

        if (!"ANNIVERSARY".equals(todo.getType())) {
            throw new InvalidRequestException("해당 항목은 기념일이 아닙니다.");
        }

        toDoRepository.delete(todo);

        return "기념일이 성공적으로 삭제되었습니다.";
    }

    @Transactional
    public String deleteTravelById(Long id) {
//        String username = getCurrentUsername();
        User user = userService.getCurrentUser();
        ToDo todo = toDoRepository.findByIdAndUsername(id, user.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("해당 여행 일정은 등록되어 있지 않습니다."));

        if (!"TRAVEL".equals(todo.getType())) {
            throw new InvalidRequestException("해당 여행 일정은 등록되어 있지 않습니다.");
        }

        List<Media> mediaList = todo.getMedia();
        for (Media media : mediaList) {
            // 파일 삭제 (선택)
            Path filePath = Paths.get("uploads")
                    .resolve(user.getUsername())
                    .resolve("travel")
                    .resolve(Paths.get(media.getMediaUrl()).getFileName().toString());

            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                e.printStackTrace(); // 실패해도 일단 무시
            }

            // DB에서 삭제
            mediaRepository.delete(media);
        }

        toDoRepository.delete(todo);

        return "기념일이 성공적으로 삭제되었습니다.";
    }
}
