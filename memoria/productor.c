#include "../include/estructura_memoria.h"
#include "../include/gestion_memoria.h"
#include "../include/bitacora.h"
#include <pthread.h>

void *crearProceso(void *arg) {
    int pid = getpid();
    int size = (rand() % 20) + 1;  // Tamaño aleatorio entre 1 y 20 unidades


    int shmid = shmget(CLAVE_MEMORIA, sizeof(int), 0666);
    int *mem_n = (int *)shmat(shmid, NULL, 0);
    int n = mem_n[0];  // Leer `n` desde la memoria compartida
    LineaMemoria *mem = (LineaMemoria *)(mem_n + 1);

    int shmid = shmget(CLAVE_MEMORIA, MAX_LINEAS * sizeof(LineaMemoria), 0666);
    if (shmid == -1) {
        printf("----- PRODUCTOR  --------");
        perror("Error al obtener memoria compartida");
        exit(1);
    }

    int semid = semget(CLAVE_SEMAFORO, 1, 0666);
    if (semid == -1) {
        printf("----- PRODUCTOR --------");
        perror("Error al obtener semáforo");
        exit(1);
    }

    LineaMemoria *mem = (LineaMemoria *)shmat(shmid, NULL, 0);
    if ((void *)mem == (void *)-1) {
        printf("----- PRODUCTOR --------");
        perror("Error al adjuntar memoria compartida");
        exit(1);
    }

    int tipoAsignacion = rand() % 3;  // 0 = Best-Fit, 1 = First-Fit, 2 = Worst-Fit

    switch (tipoAsignacion) {
        case 0: bestFit(mem, semid, pid, size); break;
        case 1: firstFit(mem, semid, pid, size); break;
        case 2: worstFit(mem, semid, pid, size); break;
    }

    registrar_evento("Proceso creado", pid, 0, 0);  // Registro en bitácora
    shmdt(mem);

    sleep((rand() % 5) + 1);  // Simula duración de ejecución
    printf("Proceso %d terminado\n", pid);

    return NULL;
}

int main() {
    srand(time(NULL));

    for (int i = 0; i < 5; i++) {  // Crea 5 procesos aleatorios
        pthread_t hilo;
        pthread_create(&hilo, NULL, crearProceso, NULL);
        pthread_detach(hilo);
        sleep(1);
    }

    sleep(10);  // Permite que los procesos terminen
    return 0;
}