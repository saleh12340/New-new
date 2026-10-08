package com.newnew.invoice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat

data class InvoiceItem(val name:String="", val qty:String="", val price:String="", val amount:String="")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { InvoiceApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceApp() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(colorScheme = lightColorScheme(primary=Color(0xFF126B45), background=Color(0xFFF4F6F5))) {
            var customer by remember { mutableStateOf("") }
            var items by remember { mutableStateOf(listOf(InvoiceItem())) }
            val total = items.sumOf { (it.amount.toDoubleOrNull() ?: ((it.qty.toDoubleOrNull() ?: 0.0)*(it.price.toDoubleOrNull() ?: 0.0))) }
            Scaffold(
                topBar={ TopAppBar(title={Text("فاتورة بيع",fontWeight=FontWeight.Bold)},actions={IconButton({}){Icon(Icons.Default.Save,"حفظ")}}) },
                floatingActionButton={ FloatingActionButton(onClick={items=items+InvoiceItem()}){Icon(Icons.Default.Add,"إضافة صنف")} }
            ){ p ->
                Column(Modifier.fillMaxSize().background(Color(0xFFF4F6F5)).padding(p).padding(10.dp),verticalArrangement=Arrangement.spacedBy(7.dp)) {
                    Surface(shape=RoundedCornerShape(15.dp),color=Color.White,modifier=Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(9.dp),verticalArrangement=Arrangement.spacedBy(7.dp)) {
                            Text("فاتورة البيع",fontWeight=FontWeight.Bold,fontSize=17.sp)
                            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)){
                                Field("رقم الفاتورة",Modifier.weight(1f),"تلقائي")
                                Field("التاريخ",Modifier.weight(1f),"اليوم")
                            }
                            Field("العميل",Modifier.fillMaxWidth(),customer){customer=it}
                        }
                    }
                    LazyColumn(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(7.dp)){
                        itemsIndexed(items){ i,item ->
                            ItemBlock(i,item,{u->items=items.toMutableList().also{it[i]=u}}){if(items.size>1)items=items.filterIndexed{k,_->k!=i}}
                        }
                    }
                    Surface(shape=RoundedCornerShape(15.dp),color=Color.White,modifier=Modifier.fillMaxWidth()){
                        Column(Modifier.padding(11.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){
                            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("الإجمالي",fontWeight=FontWeight.Bold);Text(money(total),fontWeight=FontWeight.Bold,fontSize=19.sp)}
                            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)){Field("المدفوع",Modifier.weight(1f),"");Field("المتبقي",Modifier.weight(1f),money(total),readOnly=true)}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemBlock(index:Int,item:InvoiceItem,onChange:(InvoiceItem)->Unit,onDelete:()->Unit){
    Surface(shape=RoundedCornerShape(15.dp),color=Color.White,modifier=Modifier.fillMaxWidth()){
        Column(Modifier.padding(7.dp),verticalArrangement=Arrangement.spacedBy(5.dp)){
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Text("الصنف "+(index+1),fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f));IconButton(onDelete,Modifier.size(34.dp)){Icon(Icons.Default.Delete,"حذف",tint=MaterialTheme.colorScheme.error)}}
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(5.dp)){
                Field("المبلغ",Modifier.weight(1f),item.amount){onChange(item.copy(amount=it))}
                Field("السعر",Modifier.weight(1f),item.price){onChange(item.copy(price=it))}
                Field("العدد",Modifier.weight(.78f),item.qty){onChange(item.copy(qty=it))}
                Field("البيان",Modifier.weight(1.55f),item.name){onChange(item.copy(name=it))}
            }
            HorizontalDivider()
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(5.dp)){
                Field("الإجمالي",Modifier.weight(1f),computed(item),readOnly=true)
                Field("سعر الوحدة",Modifier.weight(1f),unit(item),readOnly=true)
                Field("الكمية",Modifier.weight(.78f),item.qty)
                Field("الصنف",Modifier.weight(1.55f),item.name)
            }
        }
    }
}

@Composable
fun Field(hint:String,modifier:Modifier,value:String,onChange:(String)->Unit={},readOnly:Boolean=false){
    OutlinedTextField(value=value,onValueChange=onChange,readOnly=readOnly,singleLine=true,placeholder={Text(hint,fontSize=12.sp)},textStyle=LocalTextStyle.current.copy(fontSize=13.sp,fontWeight=FontWeight.Medium),modifier=modifier.height(48.dp),shape=RoundedCornerShape(11.dp))
}
fun computed(i:InvoiceItem)=money(i.amount.toDoubleOrNull() ?: ((i.qty.toDoubleOrNull()?:0.0)*(i.price.toDoubleOrNull()?:0.0)))
fun unit(i:InvoiceItem):String{val q=i.qty.toDoubleOrNull()?:return "";if(q==0.0)return "";return money((i.amount.toDoubleOrNull()?:((i.price.toDoubleOrNull()?:0.0)*q))/q)}
fun money(v:Double)=DecimalFormat("#,##0.##").format(v)
