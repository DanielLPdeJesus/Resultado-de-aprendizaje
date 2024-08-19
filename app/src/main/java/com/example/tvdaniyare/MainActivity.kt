package com.example.tvdaniyare

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.tv.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tvdaniyare.ui.theme.TvdaniyareTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import okhttp3.OkHttpClient
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


data class Business(
    val id: String,
    val business_name: String,
    val business_address: String,
    val owner_name: String,
    val services_offered: String,
    val email: String,
    val phone_number: String,
    val profile_image: String,
    val business_images: List<String>,
    val opening_hours: Map<String, String>,
    val calificacion_promedio: Double,
    val numero_gustas: Int,
    val numero_resenas: Int
)

data class BusinessResponse(
    val businesses: List<Business>,
    val success: Boolean
)

interface BusinessApiService {
    @GET("Services/api/businesses")
    suspend fun getBusinesses(): BusinessResponse
}

class BusinessViewModel : ViewModel() {
    private val apiService: BusinessApiService

    init {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        val client = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://jaydey.pythonanywhere.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(BusinessApiService::class.java)
    }

    var businesses by mutableStateOf<List<Business>>(emptyList())
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun fetchBusinesses() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = apiService.getBusinesses()
                businesses = response.businesses
                error = null
                Log.d("BusinessViewModel", "Businesses fetched successfully: ${businesses.size}")
            } catch (e: Exception) {
                error = "Error al cargar los negocios: ${e.message}"
                Log.e("BusinessViewModel", "Error fetching businesses", e)
            } finally {
                isLoading = false
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TvdaniyareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val businessViewModel: BusinessViewModel = viewModel()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") { ButtonScreen(navController, businessViewModel) }
        composable("about") { AboutScreen(navController) }
        composable("businesses") { BusinessListScreen(navController, businessViewModel) }
        composable("businessDetail/{businessId}") { backStackEntry ->
            val businessId = backStackEntry.arguments?.getString("businessId")
            businessId?.let { id ->
                val business = businessViewModel.businesses.find { it.id == id }
                if (business != null) {
                    BusinessDetailScreen(navController, business)
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ButtonScreen(navController: androidx.navigation.NavController, viewModel: BusinessViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        items(listOf("Lista de negocios", "Acerca de", "Salir")) { text ->
            Button(
                onClick = {
                    when (text) {
                        "Lista de negocios" -> {
                            Log.d("ButtonScreen", "Lista de negocios button clicked")
                            viewModel.fetchBusinesses()
                            navController.navigate("businesses")
                        }
                        "Acerca de" -> navController.navigate("about")
                        "Salir" -> {
                            Log.d("ButtonScreen", "Salir button clicked")
                        }
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text)
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AboutScreen(navController: androidx.navigation.NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Text("Integrantes del equipo:") }
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = R.drawable.daniel_photo,
                    contentDescription = "Foto de Daniel",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 8.dp),
                    contentScale = ContentScale.Crop
                )
                Text("1. Daniel de Jesus Lopez Perez")
            }
        }
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = R.drawable.yareni_photo,
                    contentDescription = "Foto de Yareni",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 8.dp),
                    contentScale = ContentScale.Crop
                )
                Text("2. Yareni Yuritza Ramos Santiago")
            }
        }
        item { Text("Grado: 9") }
        item { Text("Grupo: B") }
        item { Text("Materia: DESARROLLO PARA DISPOSITIVOS INTELIGENTES") }
        item { Text("Profesor: Dr. Armando Méndez Morales") }
        item { Text("Cuatrimestre: 9") }
        item { Text("Año: 2024") }
        item {
            Button(
                onClick = {
                    Log.d("AboutScreen", "Back button clicked")
                    navController.navigateUp()
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Volver")
            }
        }
    }
}
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun BusinessListScreen(navController: androidx.navigation.NavController, viewModel: BusinessViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Lista de Negocios",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (viewModel.isLoading) {
            item { Text("Cargando...") }
        } else if (viewModel.error != null) {
            item {
                Text("Error: ${viewModel.error}")
                Log.e("BusinessListScreen", "Error displayed: ${viewModel.error}")
            }
        } else {
            items(viewModel.businesses) { business ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    onClick = { navController.navigate("businessDetail/${business.id}") }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = business.profile_image,
                            contentDescription = "Imagen del negocio",
                            modifier = Modifier
                                .size(100.dp)
                                .padding(end = 16.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column {
                            Text(business.business_name, style = MaterialTheme.typography.titleMedium)
                            Text(business.services_offered, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            item {
                Text(
                    "Total de negocios: ${viewModel.businesses.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        item {
            Button(
                onClick = { navController.navigateUp() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Volver")
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun BusinessDetailScreen(navController: androidx.navigation.NavController, business: Business) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AsyncImage(
                model = business.profile_image,
                contentDescription = "Imagen del negocio",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        }
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    business.business_name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text("Dirección: ${business.business_address}")
                Text("Propietario: ${business.owner_name}")
                Text("Servicios: ${business.services_offered}")
                Text("Email: ${business.email}")
                Text("Teléfono: ${business.phone_number}")

                Spacer(modifier = Modifier.height(8.dp))

                Text("Horario de apertura:", fontWeight = FontWeight.Bold)
                business.opening_hours.forEach { (key, value) ->
                    Text("$key: $value")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Calificación promedio: ${business.calificacion_promedio}")
                Text("Número de me gusta: ${business.numero_gustas}")
                Text("Número de reseñas: ${business.numero_resenas}")
            }
        }

        item {
            Text("Imágenes del negocio:",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        items(business.business_images) { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = "Imagen adicional del negocio",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp),
                contentScale = ContentScale.Crop
            )
        }

        item {
            Button(
                onClick = { navController.navigateUp() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Volver")
            }
        }
    }
}