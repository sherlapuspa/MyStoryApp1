package com.dicoding.mystoryapp.adapt

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ItemPostBinding
import com.dicoding.mystoryapp.data.ListStoryItem
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class ListStoryAdapt(private val listStory: List<ListStoryItem>) :
    RecyclerView.Adapter<ListStoryAdapt.ListVH>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListVH {
        val binding =
            ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListVH(binding)
    }

    override fun onBindViewHolder(holder: ListVH, itemIndex: Int) {
        holder.bind(listStory[itemIndex])
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listStory[holder.adapterPosition])
        }
    }

    class ListVH(private var binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            binding.postItem = data
            binding.executePendingBindings()
        }
    }

    override fun getItemCount(): Int = listStory.size

    companion object {

        @JvmStatic
        @BindingAdapter("setImg")
        fun setImg(imgView: ImageView, url: String) {
            Glide.with(imgView)
                .load(url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .fallback(R.drawable.ic_launcher_foreground)
                .into(imgView)
        }

        @JvmStatic
        fun formatDateToString(context: Context, dateString: String): String {
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            dateFormatter.timeZone = TimeZone.getTimeZone("UTC") // Set time zone to UTC

            val dateTime: Date?
            var formattedDate = ""

            try {
                dateTime = dateFormatter.parse(dateString)

                val dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, context.resources.configuration.locale)
                formattedDate = dateTimeFormat.format(dateTime!!)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return formattedDate
        }
    }
}
