package com.epam.esm.repository.api;

import com.epam.esm.domain.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {

}
