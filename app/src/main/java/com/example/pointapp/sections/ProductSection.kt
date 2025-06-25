//package com.example.pointapp.sections
//
//import android.view.*
//import android.widget.*
//import androidx.recyclerview.widget.RecyclerView
//import androidx.room.parser.Section
//import com.bumptech.glide.Glide
//import com.example.pointapp.R
//import com.example.pointapp.model.Product
//
//class ProductSection(
//    val header: String,
//    val products: List<Product>,
//    val onAddClick: (Product) -> Unit
//) : Section(
//    SectionParameters.builder()
//        .itemResourceId(R.layout.item_product)
//        .headerResourceId(R.layout.section_header)
//        .build()
//) {
//    override fun getContentItemsTotal() = products.size
//    override fun getItemViewHolder(view: View) = ProductVH(view)
//    override fun getHeaderViewHolder(view: View) = HeaderVH(view)
//
//    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
//        val vh = holder as ProductVH
//        val p = products[position]
//        vh.tvName.text = p.name
//        vh.tvDesc.text = p.desc
//        vh.tvPrice.text = "${p.price.toInt()}đ"
//        Glide.with(vh.img).load(p.imageUrl).into(vh.img)
//        vh.btnAdd.setOnClickListener { onAddClick(p) }
//    }
//
//    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
//        (holder as HeaderVH).tvHeader.text = header
//    }
//
//    class ProductVH(view: View) : RecyclerView.ViewHolder(view) {
//        val img = view.findViewById<ImageView>(R.id.imgProduct)
//        val tvName = view.findViewById<TextView>(R.id.txtProductName)
//        val tvDesc = view.findViewById<TextView>(R.id.tvProductDesc)
//        val tvPrice = view.findViewById<TextView>(R.id.txtProductPrice)
//        val btnAdd = view.findViewById<Button>(R.id.btnAdd)
//    }
//
//    class HeaderVH(view: View) : RecyclerView.ViewHolder(view) {
//        val tvHeader = view.findViewById<TextView>(R.id.tvHeader)
//    }
//}
