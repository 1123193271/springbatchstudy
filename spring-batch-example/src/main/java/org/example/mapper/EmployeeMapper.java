package org.example.mapper;

import org.example.domain.Employee;

public interface EmployeeMapper {

    /**
     * 添加
     */
    int save(Employee employee);

    /**
     * add additional data temp
     * @param employee
     * @return
     */
    int saveTemp(Employee employee);

    void truncateAll();

    void truncateTemp();


}
