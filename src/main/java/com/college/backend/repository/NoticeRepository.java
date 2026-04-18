package com.college.backend.repository;

import com.college.backend.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

//responsible for working(talking) to db and handles database
//Spring automatically gives you save,findAll,findById,deleteById,count many more without writing sql
public interface NoticeRepository extends JpaRepository<Notice, Long> { //JpaRepository is an interface that gives ready-made database CRUD methods without writing SQL
    List<Notice> findAllByOrderByIdDesc(); //select all records by id in descending order

}
