package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 员工数据访问接口
 * 提供了员工相关的数据库操作方法
 */
@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工信息
     * @param username 用户名
     * @return 返回对应用户名的员工对象，如果未找到则返回null
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 插入新的员工记录
     * @param employee 要插入的员工对象
     */
    @Insert("insert into employee(name, username, password, phone, sex, id_number, create_time, update_time,create_user,update_user, status) " +
            "values" +
            "(#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}, 1)")
    @AutoFill(value = OperationType.INSERT)
    void insert(Employee employee);

/**
 * 分页查询员工信息
 *
 * @param employeePageQueryDIO
 * @return
 */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDIO);

    @AutoFill(value = OperationType.UPDATE)
    void update(Employee employee);

@Select("select * from employee where id = #{id}")
    Employee getById(Long id);
}
