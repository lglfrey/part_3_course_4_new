package com.example.third_part.Repositories;

import com.example.third_part.Models.Friendship;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipRepository extends CrudRepository<Friendship, Long> {
}
