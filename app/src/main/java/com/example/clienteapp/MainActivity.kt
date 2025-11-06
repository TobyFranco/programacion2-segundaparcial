package com.example.clienteapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clienteapp.ui.theme.ClienteappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClienteappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF1a1a1a)
                ) {
                    MenuPrincipal()
                }
            }
        }
    }

    @Composable
    fun MenuPrincipal() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1a1a1a),
                            Color(0xFF0d0d0d)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "📱 ClienteApp",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00ff88),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Sistema de Gestión de Clientes",
                    fontSize = 16.sp,
                    color = Color(0xFF888888),
                    modifier = Modifier.padding(bottom = 48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón Registrar Cliente
                MenuButton(
                    text = "📋 Registrar Cliente",
                    description = "Formulario con captura de fotos",
                    color = Color(0xFF2196F3)
                ) {
                    startActivity(Intent(this@MainActivity, ClienteFormActivity::class.java))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón Cargar Archivos
                MenuButton(
                    text = "📦 Cargar Archivos ZIP",
                    description = "Selección múltiple y compresión",
                    color = Color(0xFFFF9800)
                ) {
                    startActivity(Intent(this@MainActivity, UploadFilesActivity::class.java))
                }

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Programación 2 - Segunda Parcial",
                    fontSize = 12.sp,
                    color = Color(0xFF555555)
                )
            }
        }
    }

    @Composable
    fun MenuButton(
        text: String,
        description: String,
        color: Color,
        onClick: () -> Unit
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = color
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = text,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
