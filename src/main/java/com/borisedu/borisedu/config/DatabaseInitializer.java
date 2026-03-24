package com.borisedu.borisedu.config;

import com.borisedu.borisedu.entity.*;
import com.borisedu.borisedu.repository.*;
import com.borisedu.borisedu.utils.enums.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DatabaseInitializer  implements CommandLineRunner {

    private final UserRepo userRepo;
    private final ClassRepo classRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final ScheduleRepo scheduleRepo;
    private final SubjectRepo subjectRepo;
    private final ExamScheduleRepo examScheduleRepo;
    private final AttendanceRepo attendanceRepo;
    private final ScoreTypeRepo scoreTypeRepo;
    private final ScoreRepo scoreRepo;


    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // 1. SINH DỮ LIỆU ROLE (Giữ nguyên)
        if (roleRepo.count() == 0) {
            for (RoleEnum roleEnum : RoleEnum.values()) {
                RoleEntity role = new RoleEntity();
                role.setName(roleEnum);
                roleRepo.save(role);
            }
            System.out.println("✅ Đã tạo thành công 3 Roles!");
        }

        // 2. SINH DỮ LIỆU CLASS (Giữ nguyên)
        if (classRepo.count() == 0) {
            List<ClassEntity> allClasses = new ArrayList<>();
            for (int i = 1; i <= 9; i++) {
                String schoolName = (i <= 5) ? "Tiểu học Kiến Vàng" : "THCS Kiến Vàng";
                for (String suffix : new String[]{"A", "B"}) {
                    ClassEntity classEntity = new ClassEntity();
                    classEntity.setClassName(i + suffix);
                    classEntity.setSchoolName(schoolName);
                    classEntity.setAcademicYear("2025-2026");
                    allClasses.add(classEntity);
                }
            }
            for (int i = 10; i <= 12; i++) {
                for (int j = 1; j <= 9; j++) {
                    ClassEntity classEntity = new ClassEntity();
                    classEntity.setClassName(i + "A" + j);
                    classEntity.setSchoolName("THPT Kiến Vàng");
                    classEntity.setAcademicYear("2025-2026");
                    allClasses.add(classEntity);
                }
            }
            classRepo.saveAll(allClasses);
            System.out.println("✅ Đã tạo thành công " + allClasses.size() + " Lớp học!");
        }

        // 3. SINH DỮ LIỆU USER (PHỤ HUYNH, HỌC SINH VÀ GIÁO VIÊN - ĐƠN BẢNG)
        if (userRepo.count() == 0) {
            RoleEntity parentRole = roleRepo.findByName(RoleEnum.PARENT).orElseThrow();
            RoleEntity teacherRole = roleRepo.findByName(RoleEnum.TEACHER).orElseThrow();
            RoleEntity studentRole = roleRepo.findByName(RoleEnum.STUDENT).orElseThrow(); // Đảm bảo có Role STUDENT

            List<ClassEntity> savedClasses = classRepo.findAll();
            Random random = new Random();

            String[] hoList = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Vũ", "Đinh", "Đỗ", "Lý", "Bùi"};
            String[] demNamList = {"Văn", "Xuân", "Minh", "Gia", "Đức", "Thành", "Quang", "Hải", "Thanh"};
            String[] demNuList = {"Thị", "Ngọc", "Thu", "Thanh", "Mai", "Hải", "Thùy", "Trúc", "Kim"};
            String[] tenNamList = {"Cường", "Anh", "Bảo", "Huy", "Khánh", "Đạt", "Phúc", "Nam", "Tùng", "Vinh"};
            String[] tenNuList = {"Linh", "Vy", "Nhi", "Mai", "Hoa", "Yến", "Lan", "Hà", "Tâm", "Cúc"};
            String[] addressList = {"Thanh Mỹ, Sơn Tây", "Quang Trung, Sơn Tây", "Lê Lợi, Sơn Tây", "Ngô Quyền, Sơn Tây", "Phú Thịnh, Sơn Tây"};

            // --- 3.1 TẠO 10 PHỤ HUYNH VÀ 20 HỌC SINH ---
            for (int i = 1; i <= 10; i++) {
                String hoGiaDinh = hoList[random.nextInt(hoList.length)];
                boolean isFather = random.nextBoolean();
                String demPhuHuynh = isFather ? demNamList[random.nextInt(demNamList.length)] : demNuList[random.nextInt(demNuList.length)];
                String tenPhuHuynh = isFather ? tenNamList[random.nextInt(tenNamList.length)] : tenNuList[random.nextInt(tenNuList.length)];

                UserEntity parent = new UserEntity();
                parent.setFirstName(tenPhuHuynh);
                parent.setMiddleName(demPhuHuynh);
                parent.setLastName(hoGiaDinh);
                parent.setUsername("phuhuynh" + i);
                parent.setEmail("phuhuynh" + i + "@gmail.com");
                parent.setPhone("03794800" + (i < 10 ? "6" + i : i));
                parent.setPassword(passwordEncoder.encode("123456"));
                parent.getRoles().add(parentRole);
                parent.setAvatarUrl("https://i.pravatar.cc/150?u=parent" + i);

                UserEntity savedParent = userRepo.save(parent);

                // Tạo 2 Học sinh cho mỗi phụ huynh (Học sinh giờ cũng là UserEntity)
                for (int j = 1; j <= 2; j++) {
                    boolean isBoy = random.nextBoolean();
                    String demHocSinh = isBoy ? demNamList[random.nextInt(demNamList.length)] : demNuList[random.nextInt(demNuList.length)];
                    String tenHocSinh = isBoy ? tenNamList[random.nextInt(tenNamList.length)] : tenNuList[random.nextInt(tenNuList.length)];

                    UserEntity student = new UserEntity();
                    student.setFirstName(tenHocSinh);
                    student.setMiddleName(demHocSinh);
                    student.setLastName(hoGiaDinh);

                    // Cấp tài khoản cho Học sinh đăng nhập app
                    int studentIndex = (i * 10) + j;
                    student.setUsername("hocsinh" + studentIndex);
                    student.setPassword(passwordEncoder.encode("123456"));
                    student.getRoles().add(studentRole);

                    // Các trường thuộc tính riêng của học sinh
                    student.setGender(isBoy ? GenderEnum.MALE : GenderEnum.FEMALE);
                    student.setStatus(StatusEnum.LEARNING);
                    student.setAddress(addressList[random.nextInt(addressList.length)]);
                    student.setDateOfBirth(Instant.now().minus(365L * (6 + random.nextInt(10)), ChronoUnit.DAYS));
                    student.setAvatarUrl("https://i.pravatar.cc/150?u=student" + studentIndex);

                    String hoNguoiKia = hoList[random.nextInt(hoList.length)];
                    String tenNguoiKia = isFather
                            ? hoNguoiKia + " " + demNuList[random.nextInt(demNuList.length)] + " " + tenNuList[random.nextInt(tenNuList.length)]
                            : hoNguoiKia + " " + demNamList[random.nextInt(demNamList.length)] + " " + tenNamList[random.nextInt(tenNamList.length)];
                    String tenCuaPhuHuynh = hoGiaDinh + " " + demPhuHuynh + " " + tenPhuHuynh;

                    student.setFatherName(isFather ? tenCuaPhuHuynh : tenNguoiKia);
                    student.setMotherName(isFather ? tenNguoiKia : tenCuaPhuHuynh);

                    // Nối quan hệ Tự chiếu (Self-Referencing) đến Phụ huynh
                    student.setParent(savedParent);

                    ClassEntity randomClass = savedClasses.get(random.nextInt(savedClasses.size()));
                    student.setSchoolClass(randomClass);

                    userRepo.save(student);
                }
            }
            System.out.println("✅ Đã tạo thành công 10 Phụ huynh và 20 Học sinh (Mô hình Đơn Bảng)!");

            // --- 3.2 TẠO 15 GIÁO VIÊN ---
            for (int i = 1; i <= 15; i++) {
                boolean isMaleTeacher = random.nextBoolean();
                String hoGv = hoList[random.nextInt(hoList.length)];
                String demGv = isMaleTeacher ? demNamList[random.nextInt(demNamList.length)] : demNuList[random.nextInt(demNuList.length)];
                String tenGv = isMaleTeacher ? tenNamList[random.nextInt(tenNamList.length)] : tenNuList[random.nextInt(tenNuList.length)];

                UserEntity teacher = new UserEntity();
                teacher.setFirstName(tenGv);
                teacher.setMiddleName(demGv);
                teacher.setLastName(hoGv);
                teacher.setUsername("giaovien" + i);
                teacher.setEmail("giaovien" + i + "@gmail.com");
                teacher.setPhone("09770000" + (i < 10 ? "0" + i : i));
                teacher.setPassword(passwordEncoder.encode("123456"));
                teacher.getRoles().add(teacherRole);
                teacher.setAvatarUrl("https://i.pravatar.cc/150?u=teacher" + i);

                userRepo.save(teacher);
            }
            System.out.println("✅ Đã tạo thành công 15 Giáo viên!");
        }

        // 4. SINH DỮ LIỆU SUBJECT (MÔN HỌC)
        if (subjectRepo.count() == 0) {
            String[] subjectNames = {
                    "Toán", "Ngữ Văn", "Tiếng Anh", "Vật Lý", "Hóa Học",
                    "Sinh Học", "Lịch Sử", "Địa Lý", "Giáo dục công dân",
                    "Tin học", "Thể dục", "Âm nhạc", "Mỹ thuật"
            };

            for (String name : subjectNames) {
                SubjectEntity subject = new SubjectEntity();
                subject.setName(name);
                subjectRepo.save(subject);
            }
            System.out.println("✅ Đã tạo thành công " + subjectNames.length + " Môn học!");
        }

        // 5. SINH DỮ LIỆU SCHEDULE (ĐÃ CẬP NHẬT GÁN GIÁO VIÊN)
        if (scheduleRepo.count() == 0) {
            RoleEntity teacherRole = roleRepo.findByName(RoleEnum.TEACHER).get();
            List<UserEntity> allTeachers = userRepo.findAll().stream()
                    .filter(u -> u.getRoles().contains(teacherRole))
                    .collect(Collectors.toList());
            List<ClassEntity> allClasses = classRepo.findAll();
            List<SubjectEntity> allSubjects = subjectRepo.findAll();
            List<ScheduleEntity> allSchedules = new ArrayList<>();
            Random random = new Random();

            LocalTime[] startTimes = {
                    LocalTime.of(7, 30), LocalTime.of(8, 20), LocalTime.of(9, 15),
                    LocalTime.of(10, 5), LocalTime.of(10, 55)
            };
            LocalTime[] endTimes = {
                    LocalTime.of(8, 15), LocalTime.of(9, 5), LocalTime.of(10, 0),
                    LocalTime.of(10, 50), LocalTime.of(11, 40)
            };
            DayOfWeekEnum[] schoolDays = {
                    DayOfWeekEnum.MONDAY, DayOfWeekEnum.TUESDAY,
                    DayOfWeekEnum.WEDNESDAY, DayOfWeekEnum.THURSDAY, DayOfWeekEnum.FRIDAY
            };

            for (ClassEntity clazz : allClasses) {
                for (DayOfWeekEnum day : schoolDays) {
                    for (int period = 1; period <= 5; period++) {
                        ScheduleEntity schedule = new ScheduleEntity();
                        schedule.setSchoolClass(clazz);
                        schedule.setDayOfWeek(day);
                        schedule.setPeriod(period);
                        schedule.setStartTime(startTimes[period - 1]);
                        schedule.setEndTime(endTimes[period - 1]);

                        // Random môn học
                        SubjectEntity randomSubject = allSubjects.get(random.nextInt(allSubjects.size()));
                        schedule.setSubject(randomSubject);

                        // Random giáo viên dạy tiết này
                        UserEntity randomTeacher = allTeachers.get(random.nextInt(allTeachers.size()));
                        schedule.setTeacher(randomTeacher);

                        // Logic gán phòng
                        String subjectName = randomSubject.getName();
                        if (subjectName.equalsIgnoreCase("Thể dục")) {
                            schedule.setRoom("Sân thể dục");
                        } else if (subjectName.equalsIgnoreCase("Tin học")) {
                            schedule.setRoom("Phòng máy tính 01");
                        } else if (subjectName.equalsIgnoreCase("Âm nhạc") || subjectName.equalsIgnoreCase("Mỹ thuật")) {
                            schedule.setRoom("Phòng năng khiếu");
                        } else {
                            schedule.setRoom("Phòng " + clazz.getClassName());
                        }

                        allSchedules.add(schedule);
                    }
                }
            }

            scheduleRepo.saveAll(allSchedules);
            System.out.println("✅ Đã tạo thành công Thời khóa biểu (Có Giáo Viên và Phòng Học) cho " + allClasses.size() + " lớp học!");
        }

        // 6. SINH DỮ LIỆU EXAM SCHEDULE (LỊCH THI)
        if (examScheduleRepo.count() == 0) {
            List<ClassEntity> allClasses = classRepo.findAll();
            List<SubjectEntity> allSubjects = subjectRepo.findAll();
            List<ExamScheduleEntity> allExams = new ArrayList<>();

            // Lấy ra 3 môn học giống trong thiết kế của bạn (Nếu không tìm thấy tên chính xác thì lấy đại 3 môn đầu tiên)
            SubjectEntity english = allSubjects.stream().filter(s -> s.getName().equals("Tiếng Anh")).findFirst().orElse(allSubjects.get(0));
            SubjectEntity math = allSubjects.stream().filter(s -> s.getName().equals("Toán")).findFirst().orElse(allSubjects.get(1));
            SubjectEntity literature = allSubjects.stream().filter(s -> s.getName().equals("Ngữ Văn")).findFirst().orElse(allSubjects.get(2));

            LocalDate today = LocalDate.now();

            for (ClassEntity clazz : allClasses) {
                // 1. Bài thi số 1: Đã thi xong (Cách đây 5 ngày)
                ExamScheduleEntity exam1 = new ExamScheduleEntity();
                exam1.setSchoolClass(clazz);
                exam1.setSubject(english);
                exam1.setExamType("Kiểm tra giữa kì");
                exam1.setExamDate(today.minusDays(5));
                exam1.setStartTime(LocalTime.of(8, 0));
                exam1.setEndTime(LocalTime.of(8, 45));
                exam1.setRoom("Phòng " + clazz.getClassName()); // Thi tại lớp
                allExams.add(exam1);

                // 2. Bài thi số 2: Sắp diễn ra (Trùng khớp ngày 06/04/2026 và phòng Delta 403 như thiết kế)
                ExamScheduleEntity exam2 = new ExamScheduleEntity();
                exam2.setSchoolClass(clazz);
                exam2.setSubject(math);
                exam2.setExamType("Kiểm tra cuối");
                exam2.setExamDate(LocalDate.of(2026, 4, 6));
                exam2.setStartTime(LocalTime.of(9, 0));
                exam2.setEndTime(LocalTime.of(9, 45));
                exam2.setRoom("Delta, Phòng 403");
                allExams.add(exam2);

                // 3. Bài thi số 3: Tương lai gần (Tuần sau)
                ExamScheduleEntity exam3 = new ExamScheduleEntity();
                exam3.setSchoolClass(clazz);
                exam3.setSubject(literature);
                exam3.setExamType("Kiểm tra cuối");
                exam3.setExamDate(today.plusDays(7));
                exam3.setStartTime(LocalTime.of(9, 0));
                exam3.setEndTime(LocalTime.of(9, 45));
                exam3.setRoom("Delta, Phòng 403");
                allExams.add(exam3);
            }

            // Lưu toàn bộ lịch thi vào Database
            examScheduleRepo.saveAll(allExams);
            System.out.println("✅ Đã tạo thành công Lịch thi mẫu cho " + allClasses.size() + " lớp học!");
        }

        // 7. SINH DỮ LIỆU ĐIỂM DANH (ATTENDANCE) - ĐÃ CẬP NHẬT CHO ĐƠN BẢNG
        if (attendanceRepo.count() == 0) {
            // Lấy Role STUDENT ra để lọc
            RoleEntity studentRole = roleRepo.findByName(RoleEnum.STUDENT)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Role STUDENT"));

            // Lấy danh sách Học sinh từ bảng users (Lọc theo Role)
            List<UserEntity> allStudents = userRepo.findAll().stream()
                    .filter(u -> u.getRoles().contains(studentRole))
                    .collect(Collectors.toList());

            List<ScheduleEntity> allSchedules = scheduleRepo.findAll();
            List<AttendanceEntity> allAttendances = new ArrayList<>();
            Random random = new Random();

            // Mốc thời gian: Lấy từ 8 tuần trước (khoảng giữa tháng 1/2026) cho đến ngày hôm nay
            LocalDate today = LocalDate.now();
            LocalDate startDate = today.minusWeeks(8);

            // Gom nhóm thời khóa biểu theo ID của Lớp để truy xuất cho nhanh
            Map<Long, List<ScheduleEntity>> schedulesByClass = allSchedules.stream()
                    .collect(Collectors.groupingBy(s -> s.getSchoolClass().getId()));

            // Đổi StudentEntity thành UserEntity
            for (UserEntity student : allStudents) {
                if (student.getSchoolClass() == null) continue;

                List<ScheduleEntity> classSchedules = schedulesByClass.get(student.getSchoolClass().getId());
                if (classSchedules == null || classSchedules.isEmpty()) continue;

                // Chạy vòng lặp qua từng ngày trong suốt 8 tuần qua
                for (LocalDate date = startDate; !date.isAfter(today); date = date.plusDays(1)) {

                    // Chuyển java.time.DayOfWeek sang định dạng String để so khớp với Enum của bạn
                    String currentDayName = date.getDayOfWeek().name();

                    // Lọc ra các tiết học của lớp bé này trong cái Thứ đó (VD: Lấy các tiết Thứ 2)
                    List<ScheduleEntity> schedulesToday = classSchedules.stream()
                            .filter(s -> s.getDayOfWeek().name().equals(currentDayName))
                            .collect(Collectors.toList());

                    // Tạo bản ghi điểm danh cho từng tiết học
                    for (ScheduleEntity schedule : schedulesToday) {
                        AttendanceEntity attendance = new AttendanceEntity();
                        attendance.setStudent(student); // student lúc này là UserEntity
                        attendance.setSchedule(schedule);
                        attendance.setAttendanceDate(date);

                        boolean isPresent;

                        // ==========================================
                        // TẠO HỌC SINH CÁ BIỆT ĐỂ TEST CẤM THI
                        // ==========================================
                        if (student.getUsername().equals("hocsinh11")) {
                            // Cậu bé hocsinh11 này sẽ cúp học 95% số tiết của TẤT CẢ các môn
                            isPresent = random.nextInt(100) < 5;
                        } else {
                            // Các học sinh khác vẫn đi học chăm chỉ (85% có mặt)
                            isPresent = random.nextInt(100) < 85;
                        }

                        if (isPresent) {
                            attendance.setStatus(AttendanceStatusEnum.PRESENT);
                        } else {
                            // ĐÃ SỬA Ở ĐÂY: Thay ABSENT bằng UNEXCUSED_ABSENT (Không phép)
                            // Trộn thêm logic: 80% vắng không phép, 20% vắng có phép
                            boolean isExcused = random.nextInt(100) < 20;
                            if (isExcused) {
                                attendance.setStatus(AttendanceStatusEnum.EXCUSED_ABSENT);
                            } else {
                                attendance.setStatus(AttendanceStatusEnum.UNEXCUSED_ABSENT);
                            }
                        }

                        // Khai báo mảng allAttendances ở ngoài vòng lặp
                        allAttendances.add(attendance);
                    }
                }
            }

            // Lưu toàn bộ lịch sử điểm danh vào Database
            attendanceRepo.saveAll(allAttendances);
            System.out.println("✅ Đã tạo thành công " + allAttendances.size() + " bản ghi Lịch sử Điểm danh trong 8 tuần qua (Kiến trúc Đơn bảng)!");
        }

        // 8. TẠO LOẠI ĐIỂM (SCORE TYPES)
        // ==========================================
        if (scoreTypeRepo.count() == 0) {
            scoreTypeRepo.save(ScoreTypeEntity.builder().name("Đánh giá thường xuyên").code("DGTX").coefficient(1).build());
            scoreTypeRepo.save(ScoreTypeEntity.builder().name("Đánh giá giữa kỳ").code("DGGK").coefficient(2).build());
            scoreTypeRepo.save(ScoreTypeEntity.builder().name("Đánh giá cuối kỳ").code("DGCK").coefficient(3).build());
            System.out.println("✅ Đã tạo 3 loại điểm chuẩn của Bộ GD (ĐGTX, ĐGGK, ĐGCK)!");
        }

        // ==========================================
        // 9. SINH DỮ LIỆU ĐIỂM CHO HỌC SINH
        // ==========================================
        if (scoreRepo.count() == 0) {
            ScoreTypeEntity dgtx = scoreTypeRepo.findByCode("DGTX").orElseThrow();
            ScoreTypeEntity dggk = scoreTypeRepo.findByCode("DGGK").orElseThrow();
            ScoreTypeEntity dgck = scoreTypeRepo.findByCode("DGCK").orElseThrow();

            RoleEntity studentRole = roleRepo.findByName(RoleEnum.STUDENT).orElseThrow();
            List<UserEntity> allStudents = userRepo.findAll().stream()
                    .filter(u -> u.getRoles().contains(studentRole))
                    .collect(Collectors.toList());
            List<ScheduleEntity> allSchedules = scheduleRepo.findAll();

            List<ScoreEntity> allScores = new ArrayList<>();
            Random random = new Random();

            for (UserEntity student : allStudents) {
                if (student.getSchoolClass() == null) continue;

                // Lấy danh sách các môn học của bé này (thông qua Thời khóa biểu của lớp)
                List<SubjectEntity> mySubjects = allSchedules.stream()
                        .filter(s -> s.getSchoolClass().getId().equals(student.getSchoolClass().getId()))
                        .map(ScheduleEntity::getSubject)
                        .distinct()
                        .collect(Collectors.toList());

                // Năm học giả định hiện tại
                String currentYear = student.getSchoolClass().getAcademicYear();
                if (currentYear == null) currentYear = "2025-2026";

                for (SubjectEntity subject : mySubjects) {

                    // --- HỌC KỲ 1 ---
                    // 3 cột Thường xuyên
                    for (int i = 1; i <= 3; i++) {
                        allScores.add(createScoreRecord(student, subject, dgtx, randomScore(random), 1, currentYear));
                    }
                    // 1 cột Giữa kỳ & 1 cột Cuối kỳ
                    allScores.add(createScoreRecord(student, subject, dggk, randomScore(random), 1, currentYear));
                    allScores.add(createScoreRecord(student, subject, dgck, randomScore(random), 1, currentYear));

                    // --- HỌC KỲ 2 ---
                    // 3 cột Thường xuyên
                    for (int i = 1; i <= 3; i++) {
                        allScores.add(createScoreRecord(student, subject, dgtx, randomScore(random), 2, currentYear));
                    }
                    // 1 cột Giữa kỳ
                    allScores.add(createScoreRecord(student, subject, dggk, randomScore(random), 2, currentYear));

                    // Giả sử hiện tại đang là giữa Học kỳ 2, nên chỉ có "hocsinh11" là có điểm Cuối kỳ 2 (để bạn test),
                    // còn các bạn khác chưa thi cuối kỳ.
                    if (student.getUsername().equals("hocsinh11")) {
                        allScores.add(createScoreRecord(student, subject, dgck, randomScore(random), 2, currentYear));
                    }
                }
            }
            scoreRepo.saveAll(allScores);
            System.out.println("✅ Đã tạo thành công hàng ngàn con điểm cho toàn bộ Học sinh!");
        }
    }

    // Hàm sinh điểm ngẫu nhiên từ 5.0 đến 10.0 (Làm tròn 1 chữ số thập phân)
    private Double randomScore(Random random) {
        double score = 5.0 + (random.nextDouble() * 5.0);
        return (double) Math.round(score * 10) / 10.0;
    }

    // Hàm build Object ScoreEntity cho code ở trên gọn gàng
    private ScoreEntity createScoreRecord(UserEntity student, SubjectEntity subject, ScoreTypeEntity type, Double score, int semester, String year) {
        return ScoreEntity.builder()
                .student(student)
                .subject(subject)
                .scoreType(type)
                .scoreValue(score)
                .semester(semester)
                .academicYear(year)
                .entryDate(LocalDate.now())
                .build();
    }
}
