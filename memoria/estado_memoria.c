// ----------------------- ESTADO_MEMORIA. C - ESPIA -------------

#include "../include/estructura_memoria.h"

void mostrar_memoria(LineaMemoria *mem, int n) {
    printf("Estado de la memoria:\n");
    for (int i = 0; i < n; i++) {
        printf("Línea %d → Estado: %s | PID: %d | Tamaño: %d\n", 
            i, mem[i].estado ? "Ocupado" : "Libre", mem[i].pid_ocupante, mem[i].size);
    }
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

    return 0;
}