CC = gcc
CFLAGS = -Wall -Iinclude
BIN = bin
SRC = src
ME = memoria

all: inicializador finalizador productor espia

inicializador: $(SRC)/inicializador.c $(SRC)/bitacora.c $(SRC)/semaforos.c
	$(CC) $(CFLAGS) -o $(BIN)/inicializador $(SRC)/inicializador.c $(SRC)/bitacora.c $(SRC)/semaforos.c

finalizador: $(SRC)/finalizador.c $(SRC)/bitacora.c
	$(CC) $(CFLAGS) -o $(BIN)/finalizador $(SRC)/finalizador.c $(SRC)/bitacora.c

productor: $(ME)/productor.c $(ME)/gestion_memoria.c $(SRC)/semaforos.c $(SRC)/bitacora.c
	$(CC) $(CFLAGS) -o $(BIN)/productor $(ME)/productor.c $(ME)/gestion_memoria.c $(SRC)/semaforos.c $(SRC)/bitacora.c -lpthread

espia: $(ME)/estado_memoria.c
	$(CC) $(CFLAGS) -o $(BIN)/espia $(ME)/estado_memoria.c -lpthread

clean:
	rm -f $(BIN)/* bitacora.txt