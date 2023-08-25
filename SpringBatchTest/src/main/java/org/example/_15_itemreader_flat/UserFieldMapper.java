package org.example._15_itemreader_flat;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class UserFieldMapper implements FieldSetMapper<User> {
    @Override
    public User mapFieldSet(FieldSet fieldSet) throws BindException {
        User user = new User();
        user.setId(fieldSet.readLong("id"));
        user.setAge(fieldSet.readInt("age"));
        user.setName(fieldSet.readString("name"));
        String addr = fieldSet.readString("province")+ " " + fieldSet.readString("city")
                + " " + fieldSet.readString("area");
        user.setAddress(addr);
        return user;
    }
}
