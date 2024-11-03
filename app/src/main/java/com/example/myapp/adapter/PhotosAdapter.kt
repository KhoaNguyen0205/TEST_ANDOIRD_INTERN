import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.FullImageActivity
import com.example.myapp.R

class PhotosAdapter(private val context: Context, private val photos: List<Uri>) :
    RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewPhoto: ImageView = itemView.findViewById(R.id.imageViewPhoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoUri = photos[position]
        holder.imageViewPhoto.setImageURI(photoUri)

        holder.imageViewPhoto.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, FullImageActivity::class.java).apply {
                putExtra("IMAGE_URI", photoUri)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = photos.size
}
