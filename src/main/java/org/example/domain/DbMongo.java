package org.example.domain;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Data
public class DbMongo {

    @Id
    private ObjectId id;

    private String f1;

    private String f2;

}
