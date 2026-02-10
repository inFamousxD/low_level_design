//
// Created by Aaditya on 2/9/26.
//

#include "Client.h"

#include <iostream>
#include <ostream>

Client::Client(DocumentEditor *editor) {
    this->documentEditor = editor;
}

int main(int argc, char *argv[]) {
    auto *document = new Document();
    auto *editor = new DocumentEditor(document);

    const auto client = new Client(editor);
    client->documentEditor->addText("Hey");
    client->documentEditor->addText("Hello");
    client->documentEditor->addText("World");

    for (const vector<string> render = client->documentEditor->document->render(); auto& r: render) {
        std::cout << r << std::endl;
    }

    delete client;
}
