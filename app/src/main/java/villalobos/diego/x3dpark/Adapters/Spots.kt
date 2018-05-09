package villalobos.diego.x3dpark.Adapters

import android.content.DialogInterface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycler_nearby_spots.view.*
import villalobos.diego.x3dpark.Data.Spot
import villalobos.diego.x3dpark.R

class Spots(private val spots: ArrayList<Spot>, val onClickListener: (Spot) -> Unit) :
        RecyclerView.Adapter<Spots.SpotsViewHolder>(){

    class SpotsViewHolder(val layout: View) : RecyclerView.ViewHolder(layout) {
        fun bind(spot:Spot, clickListener: (Spot) -> Unit){
            val address = "${spot.street} ${spot.number}"
            val currentFare = "${spot.current} ${spot.currency} / ${spot.rate}"
            Glide.with(layout.context).load(spot.photo).into(layout.recycler_spots_image_garage)
            layout.recycler_spots_text_address.text = address
            layout.recycler_spots_text_score.text = spot.score
            layout.recycler_spots_text_fare.text = currentFare
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotsViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_nearby_spots,parent,false)

        return SpotsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpotsViewHolder, position: Int) {
        holder.bind(spots[position],onClickListener)
    }

    override fun getItemCount(): Int {
        return spots.size
    }
}