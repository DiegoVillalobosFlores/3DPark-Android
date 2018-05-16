package villalobos.diego.x3dpark.Adapters

import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycler_vehicles.view.*
import villalobos.diego.x3dpark.Data.Vehicle
import villalobos.diego.x3dpark.R

class Vehicles(private val vehicles:ArrayList<Vehicle>,private val onClickListener: (Vehicle) -> Unit) :
        RecyclerView.Adapter<Vehicles.VehiclesViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehiclesViewHolder{
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_vehicles,parent,false)

        return VehiclesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return vehicles.size
    }

    override fun onBindViewHolder(holder: VehiclesViewHolder, position: Int) {
        holder.bind(vehicles[position],onClickListener)
    }

    class VehiclesViewHolder(val layout: View) : RecyclerView.ViewHolder(layout){
        fun bind(vehicle: Vehicle, clickListener: (Vehicle) -> Unit){
            Log.d("PROFILE REC",vehicle.toString())
            Glide.with(layout.context).load(vehicle.photo).into(layout.recycler_cars_image_photo)
            layout.recycler_cars_text_model.text = vehicle.model
            layout.recycler_cars_text_manufacturer.text = vehicle.manufacturer
            layout.recycler_cars_text_plate.text = vehicle.license
            layout.recycler_cars_view_color.background = getColorDrawable(vehicle.color)
            layout.setOnClickListener{clickListener(vehicle)}
        }

        private fun getColorDrawable(color:String) : Drawable{
            when(color){
                "Black" -> return ColorDrawable(ContextCompat.getColor(layout.context, R.color.Black))
                "Red" -> return ColorDrawable(ContextCompat.getColor(layout.context, R.color.Red))
                "Gold" -> return ColorDrawable(ContextCompat.getColor(layout.context, R.color.Gold))
                "White" -> return ColorDrawable(ContextCompat.getColor(layout.context, R.color.White))
            }
            return ColorDrawable(ContextCompat.getColor(layout.context, R.color.White))
        }
    }
}