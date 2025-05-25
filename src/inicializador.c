// --------------- INICIALIZADOR.C ------------------------------------
#include "../include/estructura_memoria.h"
#include "../include/gestion_memoria.h"
#include "../include/bitacora.h"
#include <string.h>

void inicializar_memoria(LineaMemoria *mem, int n) {
    for (int i = 0; i < n; i++) {
        mem[i].estado = 0;
        mem[i].pid_ocupante = 0;
        mem[i].size = 0;
    }
}

void inicializar_procesos(ProcesoInfo *procesos) {
    memset(procesos, 0, sizeof(ProcesoInfo) * MAX_PROCESOS);
}

int main() {
    int n;
    printf("Ingrese número de líneas de memoria (máx %d): ", MAX_LINEAS);
    scanf("%d", &n);

    if (n <= 0 || n > MAX_LINEAS) {
        fprintf(stderr, "Error: número inválido.\n");
        exit(1);
    }

    // 🔥 Corrección: usar `sizeof(int) + sizeof(LineaMemoria) * n`
    int tamano = sizeof(int) + sizeof(LineaMemoria) * n;
    
    // Asegurar alineación correcta
    if (tamano % sizeof(long) != 0) {
        tamano += sizeof(long) - (tamano % sizeof(long));
    }

    // 🔥 Eliminar segmentos previos antes de crear uno nuevo
    int shmid_antiguo = shmget(CLAVE_MEMORIA, 0, 0666);
    if (shmid_antiguo != -1) {
        shmctl(shmid_antiguo, IPC_RMID, NULL);
    }

    int shmid = shmget(CLAVE_MEMORIA, tamano, IPC_CREAT | 0666);
    if (shmid == -1) {
        perror("Error al crear memoria compartida");
        fprintf(stderr, "Intentando asignar tamaño: %d\n", tamano);
        exit(1);
    }

    int *mem_n = (int *)shmat(shmid, NULL, 0);
    if ((void *)mem_n == (void *)-1) {
        perror("Error al adjuntar memoria compartida");
        exit(1);
    }

    mem_n[0] = n;  // Guarda `n` en el primer bloque de memoria
    LineaMemoria *mem = (LineaMemoria *)(mem_n + 1);
    inicializar_memoria(mem, n);

    // 🔥 Crear memoria compartida para `ProcesoInfo`
    int shmid_procesos = shmget(CLAVE_PROCESOS, sizeof(ProcesoInfo) * MAX_PROCESOS, IPC_CREAT | 0666);
    if (shmid_procesos == -1) {
        perror("Error creando memoria compartida de procesos");
        shmdt(mem_n);
        exit(1);
    }

    ProcesoInfo *procesos = (ProcesoInfo *)shmat(shmid_procesos, NULL, 0);
    if ((void *)procesos == (void *)-1) {
        perror("Error al adjuntar memoria compartida de procesos");
        shmdt(mem_n);
        exit(1);
    }

    inicializar_procesos(procesos);

    // 🔥 No desvincular procesos todavía si se usará en el programa
    shmdt(procesos);

    int semid = semget(CLAVE_SEMAFORO, 1, IPC_CREAT | 0666);
    if (semid == -1) {
        perror("Error al crear semáforo");
        shmctl(shmid, IPC_RMID, NULL);
        shmdt(mem_n);
        exit(1);
    }

    if (semctl(semid, 0, SETVAL, 1) == -1) {
        perror("Error al configurar semáforo");
        shmctl(shmid, IPC_RMID, NULL);
        shmdt(mem_n);
        exit(1);
    }

    registrar_evento("Simulación iniciada", getpid(), 0, n - 1);
    printf("Inicialización completa.\n");

    shmdt(mem_n);
    return 0;
}