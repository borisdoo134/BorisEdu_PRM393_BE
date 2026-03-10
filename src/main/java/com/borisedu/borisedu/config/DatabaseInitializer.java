package com.borisedu.borisedu.config;

import com.borisedu.borisedu.entity.*;
import com.borisedu.borisedu.repository.*;
import com.borisedu.borisedu.utils.enums.DayOfWeekEnum;
import com.borisedu.borisedu.utils.enums.GenderEnum;
import com.borisedu.borisedu.utils.enums.RoleEnum;
import com.borisedu.borisedu.utils.enums.StatusEnum;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class DatabaseInitializer  implements CommandLineRunner {

    private final UserRepo userRepo;
    private final StudentRepo studentRepo;
    private final ClassRepo classRepo;
    private final RoleRepo roleRepo;
    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;
    private final ScheduleRepo scheduleRepo;
    private final SubjectRepo subjectRepo;

    public DatabaseInitializer(UserRepo userRepo,
                               StudentRepo studentRepo,
                               ClassRepo classRepo,
                               RoleRepo roleRepo, JwtEncoder jwtEncoder, PasswordEncoder passwordEncoder, ScheduleRepo scheduleRepo, SubjectRepo subjectRepo) {
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.classRepo = classRepo;
        this.roleRepo = roleRepo;
        this.jwtEncoder = jwtEncoder;
        this.passwordEncoder = passwordEncoder;
        this.scheduleRepo = scheduleRepo;
        this.subjectRepo = subjectRepo;
    }

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

        // 3. SINH DỮ LIỆU USER (PHỤ HUYNH, HỌC SINH VÀ GIÁO VIÊN)
        if (userRepo.count() == 0) {
            RoleEntity parentRole = roleRepo.findByName(RoleEnum.PARENT)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Role PARENT"));
            RoleEntity teacherRole = roleRepo.findByName(RoleEnum.TEACHER)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Role TEACHER"));

            List<ClassEntity> savedClasses = classRepo.findAll();
            Random random = new Random();

            // Bộ từ điển tên Tiếng Việt (Dùng chung cho cả Phụ huynh, Học sinh, Giáo viên)
            String[] hoList = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Vũ", "Đinh", "Đỗ", "Lý", "Bùi"};
            String[] demNamList = {"Văn", "Xuân", "Minh", "Gia", "Đức", "Thành", "Quang", "Hải", "Thanh"};
            String[] demNuList = {"Thị", "Ngọc", "Thu", "Thanh", "Mai", "Hải", "Thùy", "Trúc", "Kim"};
            String[] tenNamList = {"Cường", "Anh", "Bảo", "Huy", "Khánh", "Đạt", "Phúc", "Nam", "Tùng", "Vinh"};
            String[] tenNuList = {"Linh", "Vy", "Nhi", "Mai", "Hoa", "Yến", "Lan", "Hà", "Tâm", "Cúc"};
            String[] addressList = {"Thanh Mỹ, Sơn Tây", "Quang Trung, Sơn Tây", "Lê Lợi, Sơn Tây", "Ngô Quyền, Sơn Tây", "Phú Thịnh, Sơn Tây"};

            // --- 3.1 TẠO 10 PHỤ HUYNH & 20 HỌC SINH ---
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
                parent.setPhone("09870000" + (i < 10 ? "0" + i : i));
                parent.setPassword(passwordEncoder.encode("123456"));
                parent.getRoles().add(parentRole);
                parent.setAvatarUrl("https://i.pravatar.cc/150?u=parent" + i);

                UserEntity savedParent = userRepo.save(parent);

                for (int j = 1; j <= 2; j++) {
                    boolean isBoy = random.nextBoolean();
                    String demHocSinh = isBoy ? demNamList[random.nextInt(demNamList.length)] : demNuList[random.nextInt(demNuList.length)];
                    String tenHocSinh = isBoy ? tenNamList[random.nextInt(tenNamList.length)] : tenNuList[random.nextInt(tenNuList.length)];

                    StudentEntity student = new StudentEntity();
                    student.setFirstName(tenHocSinh);
                    student.setMiddleName(demHocSinh);
                    student.setLastName(hoGiaDinh);
                    student.setGender(isBoy ? GenderEnum.MALE : GenderEnum.FEMALE);
                    student.setStatus(StatusEnum.LEARNING);
                    student.setAddress(addressList[random.nextInt(addressList.length)]);

                    String hoNguoiKia = hoList[random.nextInt(hoList.length)];
                    String tenNguoiKia = isFather
                            ? hoNguoiKia + " " + demNuList[random.nextInt(demNuList.length)] + " " + tenNuList[random.nextInt(tenNuList.length)]
                            : hoNguoiKia + " " + demNamList[random.nextInt(demNamList.length)] + " " + tenNamList[random.nextInt(tenNamList.length)];
                    String tenCuaPhuHuynh = hoGiaDinh + " " + demPhuHuynh + " " + tenPhuHuynh;

                    student.setFatherName(isFather ? tenCuaPhuHuynh : tenNguoiKia);
                    student.setMotherName(isFather ? tenNguoiKia : tenCuaPhuHuynh);
                    student.setDateOfBirth(Instant.now().minus(365L * (6 + random.nextInt(10)), ChronoUnit.DAYS));
                    student.setAvatarUrl("https://i.pravatar.cc/150?u=student" + (i * 10 + j));
                    student.setParent(savedParent);

                    ClassEntity randomClass = savedClasses.get(random.nextInt(savedClasses.size()));
                    student.setSchoolClass(randomClass);

                    studentRepo.save(student);
                }
            }
            System.out.println("✅ Đã tạo thành công 10 Phụ huynh và 20 Học sinh!");

            // --- 3.2 TẠO 15 GIÁO VIÊN VỚI TÊN THẬT ---
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

                // Set avatar cho giáo viên
                teacher.setAvatarUrl("https://i.pravatar.cc/150?u=teacher" + i);

                userRepo.save(teacher);
            }
            System.out.println("✅ Đã tạo thành công 15 Giáo viên với tên thực tế!");
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

    }

}
