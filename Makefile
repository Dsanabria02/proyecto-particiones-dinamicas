CC = gcc
CFLAGS = -Wall -Iinclude
BIN = bin
SRC = src

all: inicializador finalizador

inicializador: $(SRC)/inicializador.c $(SRC)/bitacora.c $(SRC)/semaforos.c
	$(CC) $(CFLAGS) -o $(BIN)/inicializador $(SRC)/inicializador.c $(SRC)/bitacora.c $(SRC)/semaforos.c

finalizador: $(SRC)/finalizador.c $(SRC)/bitacora.c
	$(CC) $(CFLAGS) -o $(BIN)/finalizador $(SRC)/finalizador.c $(SRC)/bitacora.c

clean:
	rm -f $(BIN)/* bitacora.txt

