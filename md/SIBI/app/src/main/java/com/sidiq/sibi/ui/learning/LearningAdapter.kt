import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sidiq.sibi.databinding.ItemAlphabetBinding
import com.sidiq.sibi.domain.model.Alphabet
import dagger.hilt.android.AndroidEntryPoint

class LearningAdapter( private val listAlphabet : ArrayList<Alphabet>) : RecyclerView.Adapter<LearningAdapter.ListViewHolder>() {
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(private val binding: ItemAlphabetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(alphabet: Alphabet) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(alphabet.icon)
                    .apply(RequestOptions())
                    .into(alphabetIcon)
                itemView.setOnClickListener { onItemClickCallback?.onItemClicked(alphabet) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            ItemAlphabetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = listAlphabet.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listAlphabet[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Alphabet)
    }

}