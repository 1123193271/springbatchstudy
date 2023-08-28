package org.example.service;

import org.example.domain.Employee;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

public interface EmployeeService {
    /**
     * 保存
     */
    void save(Employee employee);

    void dataInit() throws IOException;

    /**
     * 清空数据
     */
    void truncateAll();

    /**
     * clear data of datatable:employee_temp
     */
    void truncateTemp();
}
