document.addEventListener("DOMContentLoaded", () => {
    const postForm = document.getElementById("postForm");
    const postsContainer = document.getElementById("postsContainer");

    /**
     * Función para crear el HTML de un post
     * @param {Object} post - Objeto post con datos del usuario y posible imagen
     * @returns {HTMLElement} Elemento article listo para insertar en el DOM
     */
    function createPostElement(post) {
        const postElement = document.createElement("article");
        postElement.classList.add("card", "post");

        const profileImage = post.authorImage || "https://www.pngarts.com/files/10/Default-Profile-Picture-Download-PNG-Image.png";

        let imageHTML = "";
        if (post.image && post.image.path) {
            imageHTML = `
                <div class="post-media">
                    <img src="${post.image.path}" alt="Post Image">
                </div>
            `;
        }

        postElement.innerHTML = `
            <div class="post-header">
                <img class="pfp" src="${profileImage}" alt="Profile Picture">
                <h4 class="user-name">${post.authorName} (@${post.authorUsername})</h4>
            </div>
            <p class="post-text">${post.description}</p>
            ${imageHTML}
        `;

        return postElement;
    }

    /**
     * Función para cargar todos los posts desde el servidor
     */
    async function loadPosts() {
        try {
            const response = await fetch("/GetPostsServlet");
            if (!response.ok) throw new Error("Error al obtener posts");

            const posts = await response.json();

            // Limpiar contenedor
            postsContainer.innerHTML = "";

            // Mostrar posts más recientes primero
            posts.forEach(post => {
                const postEl = createPostElement(post);
                postsContainer.prepend(postEl);
            });

        } catch (error) {
            console.error("Error al cargar posts:", error);
            postsContainer.innerHTML = "<p>No se pudieron cargar los posts.</p>";
        }
    }

    // Cargar posts al iniciar la página
    loadPosts();

    /**
     * Evento de envío del formulario de nuevo post
     */
    postForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const formData = new FormData(postForm);

        try {
            const response = await fetch("../../Source%20Packages/com.mycompany.piaweb.servlets/PostServlet.java", {
                method: "POST",
                body: formData
            });

            if (!response.ok) throw new Error("Error al crear el post");

            // Mensaje de éxito
            alert("Publicación creada correctamente!");

            // Limpiar formulario
            postForm.reset();

            // Recargar posts
            loadPosts();

        } catch (error) {
            console.error(error);
            alert("Error al crear la publicación.");
        }
    });
});
