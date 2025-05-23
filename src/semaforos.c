#include "../include/compartido.h"

void wait_semaforo(int semid) {
    struct sembuf op = {0, -1, 0};
    semop(semid, &op, 1);
}

void signal_semaforo(int semid) {
    struct sembuf op = {0, 1, 0};
    semop(semid, &op, 1);
}

