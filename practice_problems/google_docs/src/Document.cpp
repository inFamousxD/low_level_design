//
// Created by Aaditya on 2/9/26.
//

#include "Document.h"
using namespace std;

vector<DocumentElement *> Document::getElements() const {
    return documentElements;
}

void Document::addElement(DocumentElement *element) {
    documentElements.push_back(element);
}

vector<string> Document::render() const {
    vector<string> result;
    result.reserve(documentElements.size());

    for (const auto& element : documentElements) {
        result.push_back(element->render());
    }

    return result;
}
