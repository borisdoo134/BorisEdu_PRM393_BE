package com.borisedu.borisedu.config;

import com.borisedu.borisedu.entity.ClassEntity;
import com.borisedu.borisedu.entity.RoleEntity;
import com.borisedu.borisedu.entity.StudentEntity;
import com.borisedu.borisedu.entity.UserEntity;
import com.borisedu.borisedu.repository.ClassRepo;
import com.borisedu.borisedu.repository.RoleRepo;
import com.borisedu.borisedu.repository.StudentRepo;
import com.borisedu.borisedu.repository.UserRepo;
import com.borisedu.borisedu.utils.enums.GenderEnum;
import com.borisedu.borisedu.utils.enums.RoleEnum;
import com.borisedu.borisedu.utils.enums.StatusEnum;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class DatabaseInitializer  implements CommandLineRunner {

    private final UserRepo userRepo;
    private final StudentRepo studentRepo;
    private final ClassRepo classRepo;
    private final RoleRepo roleRepo;
    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(UserRepo userRepo,
                               StudentRepo studentRepo,
                               ClassRepo classRepo,
                               RoleRepo roleRepo, JwtEncoder jwtEncoder, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.classRepo = classRepo;
        this.roleRepo = roleRepo;
        this.jwtEncoder = jwtEncoder;
        this.passwordEncoder = passwordEncoder;
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

        // 3. SINH 10 PHỤ HUYNH VÀ 20 HỌC SINH VỚI TÊN THẬT
        if (userRepo.count() == 0) {
            RoleEntity parentRole = roleRepo.findByName(RoleEnum.PARENT)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Role PARENT"));

            List<ClassEntity> savedClasses = classRepo.findAll();
            Random random = new Random();

            // Bộ từ điển tên Tiếng Việt
            String[] hoList = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Vũ", "Đinh", "Đỗ", "Lý", "Bùi"};
            String[] demNamList = {"Văn", "Xuân", "Minh", "Gia", "Đức", "Thành", "Quang", "Hải", "Thanh"};
            String[] demNuList = {"Thị", "Ngọc", "Thu", "Thanh", "Mai", "Hải", "Thùy", "Trúc", "Kim"};
            String[] tenNamList = {"Cường", "Anh", "Bảo", "Huy", "Khánh", "Đạt", "Phúc", "Nam", "Tùng", "Vinh"};
            String[] tenNuList = {"Linh", "Vy", "Nhi", "Mai", "Hoa", "Yến", "Lan", "Hà", "Tâm", "Cúc"};
            String[] addressList = {"Thanh Mỹ, Sơn Tây", "Quang Trung, Sơn Tây", "Lê Lợi, Sơn Tây", "Ngô Quyền, Sơn Tây", "Phú Thịnh, Sơn Tây"};

            for (int i = 1; i <= 10; i++) {
                // Random thông tin Phụ huynh
                String hoGiaDinh = hoList[random.nextInt(hoList.length)];
                boolean isFather = random.nextBoolean(); // Random là Bố hay Mẹ
                String demPhuHuynh = isFather ? demNamList[random.nextInt(demNamList.length)] : demNuList[random.nextInt(demNuList.length)];
                String tenPhuHuynh = isFather ? tenNamList[random.nextInt(tenNamList.length)] : tenNuList[random.nextInt(tenNuList.length)];

                UserEntity parent = new UserEntity();
                parent.setFirstName(tenPhuHuynh);
                parent.setMiddleName(demPhuHuynh);
                parent.setLastName(hoGiaDinh);

                // Vẫn giữ username và SĐT có quy luật để bạn dễ dàng gõ lúc test Login
                parent.setUsername("phuhuynh" + i);
                parent.setEmail("phuhuynh" + i + "@gmail.com");
                parent.setPhone("09870000" + (i < 10 ? "0" + i : i));
                parent.setPassword(passwordEncoder.encode("123456"));
                parent.getRoles().add(parentRole);

                UserEntity savedParent = userRepo.save(parent);

                // Tạo 2 Học sinh cho mỗi phụ huynh
                for (int j = 1; j <= 2; j++) {
                    boolean isBoy = random.nextBoolean();
                    String demHocSinh = isBoy ? demNamList[random.nextInt(demNamList.length)] : demNuList[random.nextInt(demNuList.length)];
                    String tenHocSinh = isBoy ? tenNamList[random.nextInt(tenNamList.length)] : tenNuList[random.nextInt(tenNuList.length)];

                    StudentEntity student = new StudentEntity();
                    student.setFirstName(tenHocSinh);
                    student.setMiddleName(demHocSinh);
                    student.setLastName(hoGiaDinh); // Đảm bảo con mang cùng Họ với gia đình
                    student.setGender(isBoy ? GenderEnum.MALE : GenderEnum.FEMALE);
                    student.setStatus(StatusEnum.LEARNING);
                    student.setAddress(addressList[random.nextInt(addressList.length)]);

                    // Tạo tên người Vợ/Chồng còn lại để điền vào hồ sơ học sinh
                    String hoNguoiKia = hoList[random.nextInt(hoList.length)];
                    String tenNguoiKia = isFather
                            ? hoNguoiKia + " " + demNuList[random.nextInt(demNuList.length)] + " " + tenNuList[random.nextInt(tenNuList.length)]
                            : hoNguoiKia + " " + demNamList[random.nextInt(demNamList.length)] + " " + tenNamList[random.nextInt(tenNamList.length)];

                    String tenCuaPhuHuynh = hoGiaDinh + " " + demPhuHuynh + " " + tenPhuHuynh;

                    student.setFatherName(isFather ? tenCuaPhuHuynh : tenNguoiKia);
                    student.setMotherName(isFather ? tenNguoiKia : tenCuaPhuHuynh);

                    student.setDateOfBirth(Instant.now().minus(365L * (6 + random.nextInt(10)), ChronoUnit.DAYS));
                    student.setAvatarUrl("https://i.pravatar.cc/150?u=" + (i * 10 + j));

                    student.setParent(savedParent);
                    ClassEntity randomClass = savedClasses.get(random.nextInt(savedClasses.size()));
                    student.setSchoolClass(randomClass);

                    studentRepo.save(student);
                }
            }
            System.out.println("✅ Đã tạo thành công 10 Phụ huynh và 20 Học sinh với dữ liệu thực tế!");
        }
    }

}
