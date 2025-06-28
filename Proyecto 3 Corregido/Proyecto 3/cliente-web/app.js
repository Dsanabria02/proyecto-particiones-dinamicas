import {
  listarArchivos,
  crearArchivoAPI,
  crearDirectorioAPI,
  cambiarDirectorioAPI,
  verArchivoAPI,
  modificarArchivoAPI,
  loginUsuario,
  registrarUsuario
} from "./api.js";

let usuarioActual = "";
let contrasenaActual = "";
let elementoAEliminar = { nombre: "", tipo: "" };

// --- SESIÓN ---
window.iniciarSesion = function () {
  const user = document.getElementById("username").value.trim();
  const pass = document.getElementById("password").value.trim();
  if (!user || !pass) return mostrarMensaje("Debe completar ambos campos");

  loginUsuario(user, pass).then(ok => {
    if (ok) {
      usuarioActual = user;
      contrasenaActual = pass;
      mostrarDrive();
      refrescar();
    } else {
      mostrarMensaje("Usuario o contraseña incorrectos.");
    }
  });
};

window.registrarse = function () {
  const user = document.getElementById("registro-username").value.trim();
  const pass = document.getElementById("registro-password").value.trim();
  if (!user || !pass) return mostrarMensaje("Debe completar ambos campos");

  registrarUsuario(user, pass).then(ok => {
    if (ok) {
      mostrarMensaje("Usuario registrado con éxito.");
      mostrarLogin();
    } else {
      mostrarMensaje("El usuario ya existe.");
    }
  });
};

// --- VISIBILIDAD DE PANTALLAS ---
window.mostrarRegistro = function () {
  document.getElementById("login-container").style.display = "none";
  document.getElementById("registro-container").style.display = "flex";
  document.getElementById("drive-content").style.display = "none";
};

window.mostrarLogin = function () {
  document.getElementById("login-container").style.display = "flex";
  document.getElementById("registro-container").style.display = "none";
  document.getElementById("drive-content").style.display = "none";
};

function mostrarDrive() {
  document.getElementById("login-container").style.display = "none";
  document.getElementById("registro-container").style.display = "none";
  document.getElementById("drive-content").style.display = "flex";
}

// --- FUNCIONALIDAD DRIVE ---
export function refrescar() {
  if (!usuarioActual) {
    mostrarMensaje("Debe iniciar sesión primero.");
    return;
  }

  listarArchivos(usuarioActual).then(data => {
    mostrarArchivos(data);
  });
}

function mostrarArchivos(textoPlano) {
  const tabla = document.getElementById("tabla-archivos");
  tabla.innerHTML = "";

  const lineas = textoPlano.trim().split("\n");
  lineas.forEach(linea => {
    const tipo = linea.endsWith("/") ? "directorio" : "archivo";
    const nombre = linea.replace("/", "");
    const fila = document.createElement("tr");

    fila.innerHTML = `
      <td>${nombre}</td>
      <td>${tipo}</td>
      <td>
        ${tipo === "directorio"
          ? `<button onclick="cambiarDirectorio('${nombre}')">Entrar</button>
             <button onclick="mostrarConfirmacionEliminar('${nombre}', 'directorio')">Eliminar</button>`
          : `<button onclick="verArchivo('${nombre}')">Ver</button>
             <button onclick="modificarArchivo('${nombre}')">Editar</button>
             <button onclick="mostrarConfirmacionEliminar('${nombre}', 'archivo')">Eliminar</button>`}
      </td>
    `;
    tabla.appendChild(fila);
  });
}

// --- MODALES DE CREACIÓN ---
window.crearArchivoDesdeModal = function () {
  const name = document.getElementById("input-nombre-archivo").value.trim();
  const ext = document.getElementById("input-extension-archivo").value.trim();
  const content = document.getElementById("input-contenido-archivo").value;

  if (!name || !ext) return mostrarMensaje("Debe completar nombre y extensión");
  crearArchivoAPI(usuarioActual, name, ext, content).then(() => {
    cerrarModal("modal-archivo");
    refrescar();
  });
};

window.crearDirectorioDesdeModal = function () {
  const name = document.getElementById("input-nombre-carpeta").value.trim();
  if (!name) return mostrarMensaje("Debe ingresar un nombre de carpeta");
  crearDirectorioAPI(usuarioActual, name).then(() => {
    cerrarModal("modal-carpeta");
    refrescar();
  });
};

// --- ACCIONES CON ARCHIVOS ---
window.cambiarDirectorio = function (nombre) {
  cambiarDirectorioAPI(usuarioActual, nombre).then(() => refrescar());
};

window.verArchivo = function (nombre) {
  verArchivoAPI(usuarioActual, nombre).then(contenido => {
    mostrarMensaje("Contenido del archivo:\n" + contenido);
  });
};

window.modificarArchivo = function (nombre) {
  const nuevoContenido = prompt("Nuevo contenido:");
  if (nuevoContenido !== null)
    modificarArchivoAPI(usuarioActual, nombre, nuevoContenido).then(() => refrescar());
};

// --- ELIMINACIÓN CON CONFIRMACIÓN ---
window.mostrarConfirmacionEliminar = function (nombre, tipo) {
  elementoAEliminar = { nombre, tipo };
  document.getElementById("texto-confirmacion").textContent = `¿Deseás eliminar el ${tipo} "${nombre}"?`;
  document.getElementById("modal-confirmar-eliminar").style.display = "flex";
};

window.confirmarEliminar = function () {
  const { nombre, tipo } = elementoAEliminar;
  fetch(`/api/fs/delete`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username: usuarioActual, name: nombre, type: tipo })
  }).then(() => {
    cerrarModal("modal-confirmar-eliminar");
    refrescar();
  });
};

// --- MODALES GENÉRICOS ---
window.abrirModalArchivo = function () {
  document.getElementById("modal-archivo").style.display = "flex";
};

window.abrirModalCarpeta = function () {
  document.getElementById("modal-carpeta").style.display = "flex";
};

window.cerrarModal = function (id) {
  document.getElementById(id).style.display = "none";
};

window.mostrarMensaje = function (texto) {
  document.getElementById("mensaje-texto").textContent = texto;
  document.getElementById("modal-mensaje").style.display = "flex";
};

// --- INICIALIZACIÓN ---
window.onload = mostrarLogin;
