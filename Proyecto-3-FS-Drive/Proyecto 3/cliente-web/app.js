import {
  listarArchivos,
  crearArchivoAPI,
  crearDirectorioAPI,
  cambiarDirectorioAPI,
  verArchivoAPI,
  modificarArchivoAPI,
  verPropiedadesAPI,
  mostrarRuta,
  verificarExistencia,
  loginUsuario,
  registrarUsuario,
  compartirArchivoAPI,
  listSharedFiles,
  copiarArchivoAPI,
  moverArchivoAPI,
  verArchivoCompartidoAPI
} from "./api.js";

let usuarioActual = "";
let contrasenaActual = "";
let elementoAEliminar = { nombre: "", tipo: "" };
let archivoEditando = "";
let nombreACompartir = "";
let nombreAMoverOCopiar = "";
let tipoOperacion = "";
let selectedFile = "";

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

window.cerrarSesion = function () {
  usuarioActual = "";
  contrasenaActual = "";
  cambiarDirectorioAPI(usuarioActual, "root").then(() => refrescar());
  mostrarLogin();
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
    };
    
    mostrarRuta(usuarioActual); // Mostrar la ruta antes o después de listar
    listarArchivos(usuarioActual).then(data => {
      mostrarArchivos(data);
    });
}

function mostrarArchivos(textoPlano, esCompartido = false) {
  const tabla = document.getElementById("tabla-archivos");
  tabla.innerHTML = "";

  const lineas = textoPlano.trim().split("\n");
  lineas.forEach(linea => {
    const esFile = linea.startsWith("[FILE]");
    const esDir = linea.startsWith("[DIR]");
    const tipo = esDir ? "directorio" : "archivo";
    const nombreReal = linea.replace("[FILE] ", "").replace("[DIR] ", "");

    const fila = document.createElement("tr");

    let acciones = "";

    if (tipo === "directorio") {
      if (!esCompartido) {
        acciones = `
          <button onclick="cambiarDirectorio('${nombreReal}')">Entrar</button>
          <button onclick="verPropiedades('${nombreReal}')">Propiedades</button>
          <button onclick="mostrarConfirmacionEliminar('${nombreReal}', 'directorio')">Eliminar</button>
        `;
      }
    } else {
      if (esCompartido) {
        acciones = `<button onclick="verArchivoCompartido('${nombreReal}')">Ver</button>`;
      } else {
        acciones = `
          <button onclick="verArchivo('${nombreReal}')">Ver</button>
          <button onclick="editarArchivo('${nombreReal}')">Editar</button>
          <button onclick="verPropiedades('${nombreReal}')">Propiedades</button>
          <button onclick="descargarArchivo('${nombreReal}')">↓</button>
          <button onclick="abrirModalCompartir('${nombreReal}')">Compartir</button>
          <button onclick="mostrarConfirmacionEliminar('${nombreReal}', 'archivo')">Eliminar</button>
          <button onclick="abrirMenuOpciones('${nombreReal}')">⋮</button>
        `;
      }
    }

    fila.innerHTML = `
      <td>${linea}</td>
      <td>${tipo}</td>
      <td>${acciones}</td>
    `;

    tabla.appendChild(fila);
  });
}


function manejarUploadArchivo() {
  const input = document.getElementById("file-uploader");

  input.addEventListener("change", async function (event) {
    const file = event.target.files[0];
    if (!file) return;

    const nombre = file.name.split('.').slice(0, -1).join('.');
    const extension = file.name.split('.').pop();
    const contenido = await file.text();

    const existe = await verificarExistencia(usuarioActual, nombre);
    let overwrite = false;

    if (existe) {
      overwrite = confirm("Ya existe un archivo/directorio con ese nombre. ¿Deseás reemplazarlo?");
      if (!overwrite) return;
    }

    crearArchivoAPI(usuarioActual, nombre, extension, contenido, overwrite).then(() => {
      refrescar();
      mostrarMensaje("Archivo subido con éxito.");
    });

    // Limpiar input para permitir subir el mismo archivo de nuevo si se desea
    input.value = "";
  });
}

// --- MODALES DE CREACIÓN ---
window.crearArchivoDesdeModal = async function () {
  const name = document.getElementById("input-nombre-archivo").value.trim();
  const ext = document.getElementById("input-extension-archivo").value.trim();
  const content = document.getElementById("input-contenido-archivo").value;

  if (!name || !ext || !content.trim()) return mostrarMensaje("Todos los campos son obligatorios.");

  const existe = await verificarExistencia(usuarioActual, name);
  let overwrite = false;

  if (existe) {
    overwrite = confirm("Ya existe un archivo/directorio con ese nombre. ¿Deseás reemplazarlo?");
    if (!overwrite) return;
  }

  crearArchivoAPI(usuarioActual, name, ext, content, overwrite).then(() => {
    cerrarModal("modal-archivo");
    refrescar();
  });
};

window.crearDirectorioDesdeModal = async function () {
  const name = document.getElementById("input-nombre-carpeta").value.trim();
  if (!name) return mostrarMensaje("Debe ingresar un nombre de carpeta");

  const existe = await verificarExistencia(usuarioActual, name);
  let overwrite = false;

  if (existe) {
    overwrite = confirm("Ya existe un archivo/directorio con ese nombre. ¿Deseás reemplazarlo?");
    if (!overwrite) return;
  }

  crearDirectorioAPI(usuarioActual, name, overwrite).then(() => {
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

window.editarArchivo = function (nombre) {
  archivoEditando = nombre;
  verArchivoAPI(usuarioActual, nombre).then(contenido => {
    document.getElementById("editar-contenido").value = contenido;
    document.getElementById("modal-editar").style.display = "flex";
  });
};

window.guardarEdicion = function () {
  const nuevoContenido = document.getElementById("editar-contenido").value;
  if (nuevoContenido !== null)
    modificarArchivoAPI(usuarioActual, archivoEditando, nuevoContenido).then(() => {
      cerrarModal("modal-editar");
      refrescar();
    });
};

window.descargarArchivo = function (nombre) {
  verArchivoAPI(usuarioActual, nombre).then(contenido => {
    const blob = new Blob([contenido], { type: "text/plain" });
    const enlace = document.createElement("a");
    enlace.href = URL.createObjectURL(blob);
    enlace.download = nombre;
    enlace.click();
  });
};

window.verPropiedades = function (nombre) {
  console.log("Propiedades solicitadas de:", nombre);
  verPropiedadesAPI(usuarioActual, nombre)
    .then(props => mostrarMensaje("Propiedades:\n" + props))
    .catch(err => mostrarMensaje("Error al obtener propiedades: " + err.message));
};

window.subirUnNivel = function () {
  console.log("→ Subiendo un nivel...");
  cambiarDirectorioAPI(usuarioActual, "..").then(() => refrescar());
};

// Llamado desde el botón "Compartir"
window.abrirModalCompartir = function(nombre) {
  nombreACompartir = nombre;
  document.getElementById("input-usuario-destino").value = "";
  abrirModal("modal-compartir");
};

// Confirmación al presionar "Compartir"
window.confirmarCompartir = function () {
  const destino = document.getElementById("input-usuario-destino").value.trim();
  if (!destino) {
    mostrarMensaje("Debe ingresar el nombre del usuario destinatario.");
    return;
  }

  compartirArchivoAPI(usuarioActual, destino, nombreACompartir)
    .then(() => {
      mostrarMensaje("Compartido con éxito.");
      cerrarModal("modal-compartir");
    })
    .catch(err => {
      mostrarMensaje("Error al compartir: " + err.message);
      cerrarModal("modal-compartir");
    });
};

window.verCompartidos = function () {
  listSharedFiles(usuarioActual)
    .then(content => {
      mostrarArchivos(content, true);  // le pasamos true para indicar que son compartidos
    })
    .catch(err => {
      mostrarMensaje("Error al cargar compartidos: " + err.message);
    });
};

window.verArchivoCompartido = function (nombre) {
  verArchivoCompartidoAPI(usuarioActual, nombre)
    .then(contenido => mostrarMensaje("Contenido del archivo compartido:\n" + contenido))
    .catch(err => mostrarMensaje("Error al ver archivo compartido: " + err.message));
};


window.abrirMenuOpciones = function(nombre) {
  nombreAMoverOCopiar = nombre;
  showCopiarMoverModal(nombre);
};

window.showCopiarMoverModal = function (nombreArchivo) {
  selectedFile = nombreArchivo;
  document.getElementById("modal-copiar-mover-nombre").textContent = nombreArchivo;
  document.getElementById("modal-copiar-mover").style.display = "flex";
};

window.closeModalCopiarMover = function () {
  document.getElementById("modal-copiar-mover").style.display = "none";
};

window.handleCopy = function () {
  tipoOperacion = "copiar";
  closeModalCopiarMover();
  prepararYMostrarSelector(selectedFile);
};

window.handleMove = function () {
  tipoOperacion = "mover";
  closeModalCopiarMover();
  prepararYMostrarSelector(selectedFile);
};

async function prepararYMostrarSelector(nombreArchivo) {
  // Guardar el archivo actual
  nombreAMoverOCopiar = nombreArchivo;

  // Obtener las carpetas disponibles
  const res = await fetch(`/api/fs/folders?username=${usuarioActual}`);
  const carpetas = await res.json();

  // Poblar el combo de carpetas
  const select = document.getElementById("select-carpeta");
  select.innerHTML = "";
  carpetas.forEach(c => {
  const option = document.createElement("option");
  option.value = c;
  option.textContent = c;
  select.appendChild(option);
  });


  // Mostrar modal final con selector de destino
  document.getElementById("titulo-operacion").textContent = `${tipoOperacion === 'copiar' ? 'Copiar' : 'Mover'} archivo`;
  abrirModal("modal-operacion");
}

window.confirmarOperacion = async function() {
  const destino = document.getElementById("select-carpeta").value;
  try {
    if (tipoOperacion === "copiar") {
      await copiarArchivoAPI(usuarioActual, nombreAMoverOCopiar, destino);
    } else {
      await moverArchivoAPI(usuarioActual, nombreAMoverOCopiar, destino);
    }
    cerrarModal("modal-operacion");
    refrescar();
    if (tipoOperacion === "copiar") {
      await copiarArchivoAPI(usuarioActual, nombreAMoverOCopiar, destino);
    } else {
      await moverArchivoAPI(usuarioActual, nombreAMoverOCopiar, destino);
    }
    if (tipoOperacion === "copiar") {
      mostrarMensaje(`Archivo copiado con éxito.`);
    } else {
      mostrarMensaje(`Archivo movido con éxito.`);
    }
  } catch (err) {
    mostrarMensaje("Error al ejecutar la operación: " + err.message);
  }
};


window.confirmarOperacion = async function() {
  const destino = document.getElementById("select-carpeta").value;
  try {
    if (tipoOperacion === "copiar") {
      await copiarArchivoAPI(usuarioActual, nombreAMoverOCopiar, destino);
    } else {
      await moverArchivoAPI(usuarioActual, nombreAMoverOCopiar, destino);
    }
    cerrarModal("modal-operacion");
    refrescar();
    if (tipoOperacion === "copiar") {
      mostrarMensaje(`Archivo copiado con éxito.`);
    } else {
      mostrarMensaje(`Archivo movido con éxito.`);
    }
  } catch (err) {
    mostrarMensaje("Error al ejecutar la operación: " + err.message);
  }
};

// --- ELIMINACIÓN CON CONFIRMACIÓN ---
window.mostrarConfirmacionEliminar = function (nombre, tipo) {
  elementoAEliminar = { nombre, tipo };
  document.getElementById("texto-confirmacion").textContent = `¿Deseás eliminar el ${tipo} \"${nombre}\"?`;
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

window.abrirModal = function (id) {
  document.getElementById(id).style.display = "flex";
};


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
window.onload = function () {
  mostrarLogin();
  manejarUploadArchivo(); // ← activar escucha de subida
};
