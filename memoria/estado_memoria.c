#include "../include/estructura_memoria.h"
#include "../include/gestion_memoria.h"
#include <stdio.h>
#include <stdlib.h>
#include <sys/shm.h>
#include <unistd.h>
#include <string.h>

const char *estado_to_string(EstadoProceso estado) {
    switch (estado) {
        case EJECUTANDO: return "EJECUTANDO";
        case BLOQUEADO: return "BLOQUEADO";
        case ACCEDIENDO_MEMORIA: return "ACCESO MEMORIA";
        case FINALIZADO: return "FINALIZADO";
        default: return "DESCONOCIDO";
    }
}

void mostrar_memoria(LineaMemoria *mem, int n) {
    printf("Estado de la memoria:\n");
    for (int i = 0; i < n; i++) {
        printf("Línea %d → Estado: %s | PID: %d | Tamaño: %d\n", 
            i, mem[i].estado ? "Ocupado" : "Libre", mem[i].pid_ocupante, mem[i].size);
    }
}

void mostrar_procesos() {
    int shmid = shmget(CLAVE_PROCESOS, sizeof(ProcesoInfo) * MAX_PROCESOS, 0666);
    if (shmid == -1) {
        perror("Error al obtener memoria de procesos");
        return;
    }

    ProcesoInfo *procesos = (ProcesoInfo *)shmat(shmid, NULL, 0);
    if ((void *)procesos == (void *)-1) {
        perror("Error al adjuntar memoria de procesos");
        return;
    }

    printf("\nEstado de los procesos:\n");
    for (int i = 0; i < MAX_PROCESOS; i++) {
        if (procesos[i].pid != 0) {
            printf("PID %d → Estado: %s\n", procesos[i].pid, estado_to_string(procesos[i].estado));
        }
    }

    shmdt(procesos);
}

int main() {
    int shmid = shmget(CLAVE_MEMORIA, 0, 0666);
    if (shmid == -1) {
        perror("Error al obtener memoria compartida");
        exit(1);
    }

    int *mem_n = (int *)shmat(shmid, NULL, 0);
    int n = mem_n[0];
    LineaMemoria *mem = (LineaMemoria *)(mem_n + 1);
    mostrar_memoria(mem, n);
    shmdt(mem_n);

    mostrar_procesos();

    return 0;
}
