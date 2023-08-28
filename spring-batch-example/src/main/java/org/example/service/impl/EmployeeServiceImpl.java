package org.example.service.impl;

import org.example.domain.Employee;
import org.example.mapper.EmployeeMapper;
import org.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    EmployeeMapper employeeMapper;

    @Value("${job.data.path}")
    public String path;

    @Override
    public void save(Employee employee) {
        employeeMapper.save(employee);
    }

    @Override
    public void dataInit() throws IOException {
        File file = new File(path, "employee.csv");
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(file);
        String text ="";
        Random ageR = new Random();
        Random sexR = new Random();

        //给employee文件中生成五十万条数据
        long beginTime = System.currentTimeMillis();
        System.out.println("开始时间是["+ beginTime +"]");
        for (int i = 1; i <= 500000; i++) {
            if (i == 500000) {
                text = i + ",xinyolee_" + i + "," + ageR.nextInt(50) + "," + (sexR.nextBoolean()?1:0);
            } else {
                text = i + ",xinyolee_" + i + "," + ageR.nextInt(50) + "," + (sexR.nextBoolean()?1:0) +
                "\n";
            }

            outputStream.write(text.getBytes());
            outputStream.flush();
        }

        outputStream.close();
        System.out.println("总共耗时:{" + (System.currentTimeMillis() - beginTime) + "}");

    }

    @Override
    public void truncateAll() {
        employeeMapper.truncateAll();
    }

    @Override
    public void truncateTemp() {
        employeeMapper.truncateTemp();
    }
}
