// ------------------- FUNCIONES DE ARCHIVOS -------------------

// Llama al endpoint para listar el contenido del directorio actual
export async function listarArchivos(username) {
  const res = await fetch(`/api/fs/list?username=${username}`);
  return res.text(); // Devuelve texto plano con archivos y carpetas
}

// Llama al endpoint para crear un archivo nuevo
export async function crearArchivoAPI(username, name, extension, content, overwrite = false) {
  const res = await fetch("/api/fs/create-file", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, name, extension, content, overwrite })
  });
  return res.text();
}

// Llama al endpoint para crear un nuevo directorio
export async function crearDirectorioAPI(username, name, overwrite = false) {
  const res = await fetch("/api/fs/create-directory", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, name, overwrite })
  });
  return res.text();
}

// Llama al endpoint para verificar existencia de archivo/directorio
export async function verificarExistencia(username, name) {
  const res = await fetch(`/api/fs/exists?username=${username}&name=${name}`);
  return res.ok ? await res.json() : false;
}

// Llama al endpoint para cambiar de directorio
export async function cambiarDirectorioAPI(username, name) {
  const res = await fetch("/api/fs/change-directory", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, name })
  });
  return res.text();
}

// Llama al endpoint para ver el contenido de un archivo
export async function verArchivoAPI(username, name) {
  const res = await fetch(`/api/fs/view-file?username=${username}&name=${name}`);
  return res.text();
}

// Llama al endpoint para modificar el contenido de un archivo
export async function modificarArchivoAPI(username, name, content) {
  const res = await fetch("/api/fs/modify-file", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, name, content })
  });
  return res.text();
}

// Llama al endpoint para ver las propiedades del archivo
export async function verPropiedadesAPI(username, name) {
  const url = `/api/fs/view-properties?username=${encodeURIComponent(username)}&name=${encodeURIComponent(name)}`;
  const response = await fetch(url);

  if (!response.ok) {
    const error = await response.text();
    throw new Error(error);
  }

  return await response.text();
}

// Llamada al endpoint para ver ruta actual
export async function mostrarRuta(username) {
    const res = await fetch(`/api/fs/path?username=${username}`);
    const ruta = await res.text(); // Ejemplo: "/Mi Unidad/Proyectos/Notas"

    const contenedorRuta = document.getElementById("path-bar");
    contenedorRuta.textContent = ruta;

    // Ocultar botón de volver si estás en raíz
    const botonVolver = document.querySelector("button[onclick='subirUnNivel()']");
    if (ruta === "/") {
      botonVolver.style.display = "none";
    } else {
      botonVolver.style.display = "inline-block";
    }
}

export async function compartirArchivoAPI(fromUser, toUser, name) {
  // Limpiar nombre eliminando etiquetas como "[FILE] " o "[DIR] "
  if (name.startsWith("[FILE]") || name.startsWith("[DIR]")) {
    name = name.replace(/^\[\w+\]\s*/, "");
  }

  const res = await fetch("/api/fs/share", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ fromUser, toUser, name })
  });

  if (!res.ok) throw new Error("No se pudo compartir el archivo.");
  return res.text();
}

export async function listSharedFiles(username) {
  const res = await fetch(`/api/fs/shared?username=${username}`);
  if (!res.ok) throw new Error("Error al listar compartidos");
  return await res.text();
}

export async function verArchivoCompartidoAPI(username, name) {
  const res = await fetch(`/api/fs/view-shared-file?username=${username}&name=${name}`);
  if (!res.ok) throw new Error("No se pudo obtener el archivo compartido");
  return await res.text();
}

// ------------------- FUNCIONES DE AUTENTICACIÓN -------------------

// Llama al endpoint para registrar un nuevo usuario
export async function registrarUsuario(username, password) {
  const res = await fetch("/api/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password })
  });

  const text = await res.text();
  return text.toLowerCase().includes("exitoso");
}

// Llama al endpoint para iniciar sesión
export async function loginUsuario(username, password) {
  const res = await fetch("/api/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password })
  });

  const text = await res.text();
  return text.toLowerCase().includes("exitoso");
}
