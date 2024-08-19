package com.example.tvdaniyare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.tv.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Surface
import androidx.tv.material3.Button
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tvdaniyare.ui.theme.TvdaniyareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TvdaniyareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
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
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { ButtonScreen(navController) }
        composable("about") { AboutScreen(navController) }
    }
}

@Composable
fun ButtonScreen(navController: androidx.navigation.NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        items(listOf("Lista de negocios", "Acerca de", "Salir")) { text ->
            Button(
                onClick = {
                    when (text) {
                        "Acerca de" -> navController.navigate("about")
                        "Salir" -> {/* TODO: Implementar acción para Salir */}
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text)
            }
        }
    }
}

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
                Image(
                    painter = painterResource(id = R.drawable.daniel_photo),
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
                Image(
                    painter = painterResource(id = R.drawable.yareni_photo),
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
                onClick = { navController.navigateUp() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Volver")
            }
        }
    }
}