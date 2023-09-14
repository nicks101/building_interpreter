#include <stdlib.h> 
#include <stdio.h>

typedef struct Node {
    char *data;
    struct Node *next;
    struct Node *prev;
} Node;


Node* insert(char* data, Node* head){

    Node* node = (Node*) malloc(sizeof(Node));

    node->data = data;
    node->next = NULL;

    if(head == NULL){
        node->prev = NULL;
        head = node;
        return head;
    }

    Node* current = head;
    while(current->next != NULL){
        current = current->next;
    }
    current->next = node;
    node->prev = current;

    return head;
}

Node* delete(char* data, Node* head){

    if(head == NULL) return NULL;

    Node* current = head;

    while(current != NULL){
        if(data == current->data){
            // if current is the head
            if(current->prev == NULL){
                head = current->next;
                head->prev = NULL;
            }
            // if current is the tail
            else if(current->next == NULL){
                current->prev->next = NULL;
            }
            // if current is in the middle
            else{
                current->prev->next = current->next;
                current->next->prev = current->prev;
            }
            free(current);
            return head;
        }
        current = current->next;
    }
    return head;
}

void find(char* data, Node* head){
    Node* current = head;

    int index = 0;

    while(current != NULL){
        if(data == current->data){
            printf("Found %s at %d index\n", data, index);
            return;
        }
        current = current->next;
        index++;
    }
    printf("Not found %s\n", data);
}



void print_list(Node* head){
    Node* current = head;

    if(current == NULL){
        printf("List is empty\n");
        return;
    }

    printf("List: \n");

    while(current != NULL){
        printf("%s", current->data);
        printf(" -> ");
        current = current->next;
    }

    printf("NULL\n");
}


int main(){

    Node* head = NULL;
    head = insert("Hello", head);
    insert("World", head);
    insert("!", head);
    insert("This", head);
    insert("is", head);
    insert("a", head);
    insert("doubly", head);
    insert("linked", head);
    insert("list", head);
    print_list(head);

    head = delete("Hello", head);
    head = delete("!", head);
    head = delete("list", head);
    print_list(head);

    find("World", head);
    find("list", head);
    find("is", head);

    return 0;
}