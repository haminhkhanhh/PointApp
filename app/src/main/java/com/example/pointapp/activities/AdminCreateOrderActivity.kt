package com.example.pointapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.R
import com.example.pointapp.adapters.SectionedProductAdapter
import com.example.pointapp.fragments.CustomizeDrinkBottomSheet
import com.example.pointapp.model.Category
import com.example.pointapp.model.OrderItem
import com.example.pointapp.model.Product
import com.example.pointapp.model.Topping
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.text.Normalizer
import java.util.regex.Pattern

class AdminCreateOrderActivity : AppCompatActivity() {

    private lateinit var adapter: SectionedProductAdapter
    private lateinit var rvProductSections: RecyclerView
    private lateinit var searchView: SearchView

    private var categoryList: List<Category> = emptyList()
    private var productList: List<Product> = emptyList()
    private val cartList = mutableListOf<OrderItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_create_order)

        rvProductSections = findViewById(R.id.rvProductSections)
        searchView = findViewById(R.id.searchView)
        rvProductSections.layoutManager = LinearLayoutManager(this)

        loadCategoriesAndProducts {
            setupAdapter()
            setupSearchView()
        }

        findViewById<FloatingActionButton>(R.id.fabCart).setOnClickListener {
            // Gửi cartList sang màn hình thanh toán
            val intent = Intent(this, CheckoutActivity::class.java)
            intent.putParcelableArrayListExtra("cartList", ArrayList(cartList))
            startActivity(intent)
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

    }


    private fun loadCategoriesAndProducts(onLoaded: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("categories")
            .orderBy("order")
            .get()
            .addOnSuccessListener { categorySnapshot ->
                categoryList = categorySnapshot.map { doc ->
                    Category(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        order = (doc.getLong("order") ?: 0).toInt()
                    )
                }
                db.collection("products")
                    .get()
                    .addOnSuccessListener { productSnapshot ->
                        productList = productSnapshot.map { doc ->
                            Product(
                                id = doc.id,
                                name = doc.getString("name") ?: "",
                                desc = doc.getString("desc") ?: "",
                                price = doc.getDouble("price") ?: 0.0,
                                imageUrl = doc.getString("imageUrl") ?: "",
                                categoryId = doc.getString("categoryId") ?: ""
                            )
                        }
                        onLoaded()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Lỗi lấy sản phẩm: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi lấy danh mục: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Gọi setupAdapter sau mỗi lần load/filter
    private fun setupAdapter(filteredProducts: List<Product>? = null) {
        val products = filteredProducts ?: productList
        adapter = SectionedProductAdapter(
            categories = categoryList,
            products = products
        ) { product ->
            // Khi click nút +, mở BottomSheet chọn đường/đá/topping cho product này
            showCustomizeDrinkBottomSheet(product)
        }
        rvProductSections.adapter = adapter
    }

    // Tìm kiếm không dấu
    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterProductList(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProductList(newText)
                return true
            }
        })
    }

    private fun filterProductList(query: String?) {
        val lowerQuery = query?.trim()?.lowercase()?.removeVietnameseAccents() ?: ""
        if (lowerQuery.isEmpty()) {
            setupAdapter()
            return
        }
        val filteredProducts = productList.filter {
            val nameNoAccent = it.name.lowercase().removeVietnameseAccents()
            val descNoAccent = it.desc.lowercase().removeVietnameseAccents()
            nameNoAccent.contains(lowerQuery) || descNoAccent.contains(lowerQuery)
        }
        setupAdapter(filteredProducts)
    }

    // Extension: bỏ dấu tiếng Việt
    private fun String.removeVietnameseAccents(): String {
        var temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        temp = pattern.matcher(temp).replaceAll("")
        temp = temp.replace('đ', 'd').replace('Đ', 'D')
        return temp
    }

    // Hiển thị BottomSheet chọn đường/đá/topping (topping lấy từ Firestore)
    private fun showCustomizeDrinkBottomSheet(product: Product) {
        val db = FirebaseFirestore.getInstance()
        db.collection("toppings")
            .get()
            .addOnSuccessListener { snapshot ->
                val toppingList = snapshot.map { doc ->
                    Topping(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        price = (doc.getLong("price") ?: 0).toInt()
                    )
                }
                val bottomSheet = CustomizeDrinkBottomSheet(toppingList) { sugar, ice, selectedToppings ->
                    // Xử lý khi xác nhận
                    val msg = "Món: ${product.name}\nĐường: $sugar, Đá: $ice\nTopping: ${
                        selectedToppings.joinToString { "${it.name} (+${it.price}đ)" }
                    }"
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    cartList.add(OrderItem(product, sugar, ice, selectedToppings))
                    findViewById<FloatingActionButton>(R.id.fabCart).show()
                    Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show()
                }
                bottomSheet.show(supportFragmentManager, "customizeDrink")
            }
    }
}

// Hàm remove dấu tiếng Việt (nên để cuối file, hoặc move vào file Utils riêng)
fun String.removeVietnameseAccents(): String {
    var temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    temp = pattern.matcher(temp).replaceAll("")
    temp = temp.replace('đ', 'd').replace('Đ', 'D')
    return temp
}