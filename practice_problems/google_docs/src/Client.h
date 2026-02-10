//
// Created by Aaditya on 2/9/26.
//

#ifndef LOW_LEVEL_DESIGN_CLIENT_H
#define LOW_LEVEL_DESIGN_CLIENT_H
#include "DocumentEditor.h"


class Client {
public:
    DocumentEditor *documentEditor;

    explicit Client(DocumentEditor *editor);
};


#endif //LOW_LEVEL_DESIGN_CLIENT_H