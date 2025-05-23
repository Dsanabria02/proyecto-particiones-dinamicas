#include "../include/compartido.h"
#include "../include/bitacora.h"

int main() {
    int shmid = shmget(CLAVE_MEMORIA, 0, 0666);
    int semid = semget(CLAVE_SEMAFORO, 1, 0666);

    if (shmid != -1) {
        shmctl(shmid, IPC_RMID, NULL);
        printf("Memoria compartida eliminada.\n");
    }

    if (semid != -1) {
        semctl(semid, 0, IPC_RMID);
        printf("Semáforo eliminado.\n");
    }

    registrar_evento("Simulación finalizada", getpid(), 0, 0);
    return 0;
}

