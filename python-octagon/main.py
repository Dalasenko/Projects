import pygame
import math
import sys
import random

# Initialize Pygame.
pygame.init()
WIDTH, HEIGHT = 1200, 800
screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Aesthetic Rotating Octagon & Bouncing Balls")
clock = pygame.time.Clock()

# Fixed dark grey background.
DARK_GREY = (50, 50, 50)

# Global collision counter.
collision_counter = 0

# Global particle list (for collision sparkles).
particles = []

# Define color constants.
RED = (255, 50, 50)
GREY = (160, 160, 160)
WHITE = (255, 255, 255)
LIGHT_GREY = (200, 200, 200)
BLUE = (100, 180, 255)

# Use a consistent font for labels.
LABEL_FONT = pygame.font.SysFont("freesansbold", 24)
COUNTER_FONT = pygame.font.SysFont("freesansbold", 28)


# -------------------------------------------------------------------
# Slider class for interactive controls.
# -------------------------------------------------------------------
class Slider:
    def __init__(self, x, y, w, h, min_val, max_val, initial, label):
        # x, y are the top-left coordinates of the slider bar.
        self.rect = pygame.Rect(x, y, w, h)
        self.min_val = min_val
        self.max_val = max_val
        self.value = initial
        self.label = label
        self.knob_radius = h // 2
        self.knob_x = self.value_to_pos(self.value)
        self.dragging = False

    def value_to_pos(self, value):
        ratio = (value - self.min_val) / (self.max_val - self.min_val)
        return self.rect.x + int(ratio * self.rect.w)

    def pos_to_value(self, pos):
        ratio = (pos - self.rect.x) / self.rect.w
        return self.min_val + ratio * (self.max_val - self.min_val)

    def handle_event(self, event):
        if event.type == pygame.MOUSEBUTTONDOWN:
            if event.button == 1 and self.get_knob_rect().collidepoint(event.pos):
                self.dragging = True
        elif event.type == pygame.MOUSEBUTTONUP:
            if event.button == 1:
                self.dragging = False
        elif event.type == pygame.MOUSEMOTION:
            if self.dragging:
                x = min(max(event.pos[0], self.rect.x), self.rect.x + self.rect.w)
                self.knob_x = x
                self.value = self.pos_to_value(x)

    def get_knob_rect(self):
        return pygame.Rect(self.knob_x - self.knob_radius,
                           self.rect.centery - self.knob_radius,
                           self.knob_radius * 2,
                           self.knob_radius * 2)

    def draw(self, surface):
        # Draw the slider track.
        pygame.draw.rect(surface, LIGHT_GREY, self.rect, border_radius=10)
        # Draw the knob.
        pygame.draw.circle(surface, BLUE, (self.knob_x, self.rect.centery), self.knob_radius)

        # Prepare and render the label text.
        if self.label.lower() == "rotation":
            label_text = f"{self.label}: {int(self.value)}"
        else:
            label_text = f"{self.label}: {self.value:.1f}"
        label_surf = LABEL_FONT.render(label_text, True, WHITE)
        # Position the label above THIS slider bar.
        # Here, we set the label so that its midbottom is 20 pixels above the top of this slider bar.
        label_rect = label_surf.get_rect(midbottom=(self.rect.centerx, self.rect.top - 20))
        surface.blit(label_surf, label_rect)


# -------------------------------------------------------------------
# Geometry helper functions.
# -------------------------------------------------------------------
def get_octagon_vertices(center, radius, rotation_deg=0):
    cx, cy = center
    vertices = []
    offset = math.radians(rotation_deg)
    for i in range(8):
        angle = offset + i * (2 * math.pi / 8)
        x = cx + radius * math.cos(angle)
        y = cy + radius * math.sin(angle)
        vertices.append((x, y))
    return vertices


def point_to_segment_distance(px, py, ax, ay, bx, by):
    vx, vy = bx - ax, by - ay
    wx, wy = px - ax, py - ay
    c1 = vx * wx + vy * wy
    if c1 <= 0:
        return math.hypot(px - ax, py - ay), (ax, ay)
    c2 = vx * vx + vy * vy
    if c2 <= c1:
        return math.hypot(px - bx, py - by), (bx, by)
    t = c1 / c2
    proj_x = ax + t * vx
    proj_y = ay + t * vy
    return math.hypot(px - proj_x, py - proj_y), (proj_x, proj_y)


def reflect_velocity(vel, normal):
    dot = vel[0] * normal[0] + vel[1] * normal[1]
    return (vel[0] - 2 * dot * normal[0], vel[1] - 2 * dot * normal[1])


def point_in_polygon(point, polygon):
    x, y = point
    inside = False
    n = len(polygon)
    p1x, p1y = polygon[0]
    for i in range(1, n + 1):
        p2x, p2y = polygon[i % n]
        if y > min(p1y, p2y):
            if y <= max(p1y, p2y):
                if x <= max(p1x, p2x):
                    if p1y != p2y:
                        xinters = (y - p1y) * (p2x - p1x) / (p2y - p1y) + p1x
                    if p1x == p2x or x <= xinters:
                        inside = not inside
        p1x, p1y = p2x, p2y
    return inside


def correct_ball_position(ball, polygon):
    if point_in_polygon(ball.pos, polygon):
        return
    best_dist = float('inf')
    best_proj = None
    best_edge = None
    for i in range(len(polygon)):
        A = polygon[i]
        B = polygon[(i + 1) % len(polygon)]
        dist, proj = point_to_segment_distance(ball.pos[0], ball.pos[1], A[0], A[1], B[0], B[1])
        if dist < best_dist:
            best_dist = dist
            best_proj = proj
            best_edge = (A, B)
    if best_proj is None or best_edge is None:
        return
    A, B = best_edge
    edge_vec = (B[0] - A[0], B[1] - A[1])
    normal = (-edge_vec[1], edge_vec[0])
    norm_length = math.hypot(normal[0], normal[1])
    if norm_length == 0:
        return
    normal = (normal[0] / norm_length, normal[1] / norm_length)
    poly_center = (sum(v[0] for v in polygon) / len(polygon),
                   sum(v[1] for v in polygon) / len(polygon))
    to_center = (poly_center[0] - best_proj[0], poly_center[1] - best_proj[1])
    if (to_center[0] * normal[0] + to_center[1] * normal[1]) < 0:
        normal = (-normal[0], -normal[1])
    ball.pos = [best_proj[0] + normal[0] * ball.radius, best_proj[1] + normal[1] * ball.radius]
    ball.vel = list(reflect_velocity(ball.vel, normal))


# -------------------------------------------------------------------
# Ball class: A colored ball with a trail and collision effects.
# -------------------------------------------------------------------
class Ball:
    def __init__(self, pos, radius=10):
        self.pos = list(pos)
        self.radius = radius
        self.vel = [random.uniform(3, 5) * random.choice([-1, 1]),
                    random.uniform(3, 5) * random.choice([-1, 1])]
        self.acc = [0, 0.3]  # Gravity will be updated by the slider.
        self.trail = []
        self.color = RED

    def update(self, gravity):
        self.acc[1] = gravity
        self.vel[0] += self.acc[0]
        self.vel[1] += self.acc[1]
        self.pos[0] += self.vel[0]
        self.pos[1] += self.vel[1]
        self.trail.append(tuple(self.pos))
        if len(self.trail) > 50:
            self.trail.pop(0)

    def draw(self, surface):
        if len(self.trail) > 1:
            for i, pos in enumerate(self.trail):
                factor = 0.2 + 0.8 * (i / len(self.trail))
                col = (int(self.color[0] * factor),
                       int(self.color[1] * factor),
                       int(self.color[2] * factor))
                pygame.draw.circle(surface, col, (int(pos[0]), int(pos[1])),
                                   max(1, int(self.radius * factor * 0.5)))
        pygame.draw.circle(surface, self.color, (int(self.pos[0]), int(self.pos[1])), self.radius)

    def handle_collision_with_octagon(self, vertices):
        global collision_counter
        for i in range(len(vertices)):
            A = vertices[i]
            B = vertices[(i + 1) % len(vertices)]
            dist, proj = point_to_segment_distance(self.pos[0], self.pos[1], A[0], A[1], B[0], B[1])
            if dist < self.radius:
                collision_counter += 1
                edge_vec = (B[0] - A[0], B[1] - A[1])
                normal = (-edge_vec[1], edge_vec[0])
                norm = math.hypot(normal[0], normal[1])
                if norm == 0:
                    continue
                normal = (normal[0] / norm, normal[1] / norm)
                to_ball = (self.pos[0] - proj[0], self.pos[1] - proj[1])
                if (to_ball[0] * normal[0] + to_ball[1] * normal[1]) < 0:
                    normal = (-normal[0], -normal[1])
                self.vel = list(reflect_velocity(self.vel, normal))
                overlap = self.radius - dist
                self.pos[0] += normal[0] * overlap
                self.pos[1] += normal[1] * overlap
                for _ in range(3):
                    particles.append({
                        'pos': [self.pos[0], self.pos[1]],
                        'vel': [random.uniform(-3, 3), random.uniform(-3, 3)],
                        'life': 1.0
                    })

    def handle_collision_with_ball(self, other):
        global collision_counter
        dx = other.pos[0] - self.pos[0]
        dy = other.pos[1] - self.pos[1]
        dist = math.hypot(dx, dy)
        if dist == 0:
            return
        if dist < self.radius + other.radius:
            collision_counter += 1
            nx, ny = dx / dist, dy / dist
            dvx = self.vel[0] - other.vel[0]
            dvy = self.vel[1] - other.vel[1]
            p = dvx * nx + dvy * ny
            if p > 0:
                return
            self.vel[0] -= p * nx
            self.vel[1] -= p * ny
            other.vel[0] += p * nx
            other.vel[1] += p * ny
            overlap = self.radius + other.radius - dist
            self.pos[0] -= nx * overlap / 2
            self.pos[1] -= ny * overlap / 2
            other.pos[0] += nx * overlap / 2
            other.pos[1] += ny * overlap / 2
            collision_point = ((self.pos[0] + other.pos[0]) / 2,
                               (self.pos[1] + other.pos[1]) / 2)
            for _ in range(3):
                particles.append({
                    'pos': [collision_point[0], collision_point[1]],
                    'vel': [random.uniform(-3, 3), random.uniform(-3, 3)],
                    'life': 1.0
                })


# -------------------------------------------------------------------
# Update and draw particles (sparkle effect).
# -------------------------------------------------------------------
def update_and_draw_particles(surface):
    for p in particles[:]:
        p['pos'][0] += p['vel'][0]
        p['pos'][1] += p['vel'][1]
        p['life'] -= 0.03
        if p['life'] <= 0:
            particles.remove(p)
            continue
        rad = max(1, int(5 * p['life']))
        col = (255, int(255 * p['life']), int(255 * p['life']))
        pygame.draw.circle(surface, col, (int(p['pos'][0]), int(p['pos'][1])), rad)


# -------------------------------------------------------------------
# Main Program Setup.
# -------------------------------------------------------------------
# Shift the octagon upward so the bottom area is free.
octagon_center = (WIDTH // 2, HEIGHT // 2 - 100)
octagon_radius = 250

# Place two sliders with enough vertical separation so that each slider's label is clearly above it.
rotation_slider = Slider(100, HEIGHT - 200, WIDTH - 200, 20, 0, 360, 0, "Rotation")
gravity_slider = Slider(100, HEIGHT - 120, WIDTH - 200, 20, 0, 2, 0.3, "Gravity")

# Create several balls.
balls = [Ball((WIDTH // 2 + random.randint(-50, 50),
               HEIGHT // 2 - 100 + random.randint(-50, 50)), radius=12)
         for _ in range(3)]

# Main loop.
running = True
while running:
    dt = clock.tick(60) / 1000.0

    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False
        rotation_slider.handle_event(event)
        gravity_slider.handle_event(event)

    screen.fill(DARK_GREY)

    # Draw the octagon.
    octagon_vertices = get_octagon_vertices(octagon_center, octagon_radius, rotation_slider.value)
    pygame.draw.polygon(screen, GREY, octagon_vertices, width=3)

    # Update and draw balls.
    for ball in balls:
        ball.update(gravity_slider.value)
        ball.handle_collision_with_octagon(octagon_vertices)
        correct_ball_position(ball, octagon_vertices)
        if ball.pos[0] - ball.radius < 0 or ball.pos[0] + ball.radius > WIDTH:
            ball.vel[0] = -ball.vel[0]
        if ball.pos[1] - ball.radius < 0 or ball.pos[1] + ball.radius > HEIGHT:
            ball.vel[1] = -ball.vel[1]

    for i in range(len(balls)):
        for j in range(i + 1, len(balls)):
            balls[i].handle_collision_with_ball(balls[j])

    for ball in balls:
        ball.draw(screen)

    update_and_draw_particles(screen)

    # Draw each slider (its label is drawn above its own bar).
    rotation_slider.draw(screen)
    gravity_slider.draw(screen)

    counter_surf = COUNTER_FONT.render(f"Collisions: {collision_counter}", True, WHITE)
    screen.blit(counter_surf, (20, 20))

    pygame.display.flip()

pygame.quit()
sys.exit()








