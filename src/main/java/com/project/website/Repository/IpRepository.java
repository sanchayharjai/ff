package com.project.website.Repository;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.website.Model.Ip;
import com.project.website.Model.Recipe;
@Repository
public interface IpRepository extends JpaRepository<Ip, Long>{

	List<Ip> findByRecipe(Recipe recipe);

	void deleteByRecipe(Recipe recipe);

	List<Ip> findByRecipeAndCounted(Recipe recipe, boolean b);

}
