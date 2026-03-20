package com.borisedu.borisedu.entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "score_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScoreTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_type_id")
    Long id;

    // Tên loại điểm (VD: "Đánh giá thường xuyên", "Đánh giá giữa kỳ")
    @Column(nullable = false)
    String name;

    // Mã loại điểm để code dễ query (VD: "DGTX", "DGGK", "DGCK")
    @Column(nullable = false, unique = true)
    String code;

    // Hệ số điểm (VD: 1, 2, 3)
    @Column(nullable = false)
    Integer coefficient;
}