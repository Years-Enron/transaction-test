package org.example.dao;

import org.bson.types.ObjectId;
import org.example.domain.DbMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbMongoDao extends MongoRepository<DbMongo, ObjectId> {

}
