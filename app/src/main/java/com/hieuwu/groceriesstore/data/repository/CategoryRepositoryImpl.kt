package com.hieuwu.groceriesstore.data.repository

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hieuwu.groceriesstore.data.dao.CategoryDao
import com.hieuwu.groceriesstore.domain.entities.Category
import com.hieuwu.groceriesstore.domain.entities.Product
import com.hieuwu.groceriesstore.domain.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(private val categoryDao: CategoryDao) :
    CategoryRepository {

    override suspend fun getFromServer() {
        val categoriesList = mutableListOf<Category>();
        val fireStore = Firebase.firestore
        fireStore.collection("categories").get().addOnSuccessListener { result ->
            for (document in result) {
                categoriesList.add(getCategoryEntityFromDocument(document))
            }
        }
            .addOnFailureListener { exception ->
                Timber.w("Error getting documents.${exception}")
            }
        withContext(Dispatchers.IO) {
            categoryDao.insertAll(categoriesList)
        }
    }

    private fun getCategoryEntityFromDocument(document: QueryDocumentSnapshot): Category {
        val id = document.id
        val name: String = document.data["name"] as String
        val image: String = document.data["image"] as String
        return Category(id, name, image)
    }

    override fun getFromLocal() = categoryDao.getAll()
}