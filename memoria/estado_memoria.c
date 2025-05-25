#include "../include/estructura_memoria.h"

void mostrar_memoria(LineaMemoria *mem) {
    printf("Estado de la memoria:\n");
    for (int i = 0; i < MAX_LINEAS; i++) {
        printf("Línea %d → Estado: %s | PID: %d | Tamaño: %d\n", 
            i, mem[i].estado ? "Ocupado" : "Libre", mem[i].pid_ocupante, mem[i].size);
    }
}

int main() {
    int shmid = shmget(CLAVE_MEMORIA, MAX_LINEAS * sizeof(LineaMemoria), 0666);
    if (shmid == -1) {
        printf("----- ESTADO DE LA MEMORIA - ESPIA --------");
        perror("Error al obtener memoria compartida");
        exit(1);
    }

    
    LineaMemoria *mem = (LineaMemoria *)shmat(shmid, NULL, 0);
    if ((void *)mem == (void *)-1) {
        printf("----- ESTADO DE LA MEMORIA - ESPIA --------");
        perror("Error al adjuntar memoria compartida");
        exit(1);
    }


    mostrar_memoria(mem);
    shmdt(mem);

    return 0;
}