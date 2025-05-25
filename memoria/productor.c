// ---------------------- PRODUCTOR.C ------------------------------------

#include "../include/estructura_memoria.h"
#include "../include/gestion_memoria.h"
#include "../include/bitacora.h"
#include <pthread.h>

void *crearProceso(void *arg) {
    int pid = getpid();
    int size = (rand() % 20) + 1;

    int shmid = shmget(CLAVE_MEMORIA, 0, 0666);
    if (shmid == -1) {
        perror("Error al obtener memoria compartida");
        exit(1);
    }

    int *mem_n = (int *)shmat(shmid, NULL, 0);
    if ((void *)mem_n == (void *)-1) {
        perror("Error al adjuntar memoria compartida");
        exit(1);
    }

    LineaMemoria *mem = (LineaMemoria *)(mem_n + 1);

    int semid = semget(CLAVE_SEMAFORO, 1, 0666);
    if (semid == -1) {
        perror("Error al obtener semáforo");
        exit(1);
    }

    int tipoAsignacion = rand() % 3;
    switch (tipoAsignacion) {
        case 0: bestFit(mem, semid, pid, size); break;
        case 1: firstFit(mem, semid, pid, size); break;
        case 2: worstFit(mem, semid, pid, size); break;
    }

    registrar_evento("Proceso creado", pid, 0, 0);
    shmdt(mem_n);

    sleep((rand() % 5) + 1);
    printf("Proceso %d terminado\n", pid);

    return NULL;
}

int main() {
    srand(time(NULL));

    for (int i = 0; i < 5; i++) {
        pid_t pid = fork();
        if (pid == 0) {
            // Código del hijo
            crearProceso(NULL);
            exit(0);
        } else if (pid < 0) {
            perror("Error en fork");
        }
        sleep(1);
    }

    sleep(10);  // Esperar a que terminen los hijos
    return 0;
}